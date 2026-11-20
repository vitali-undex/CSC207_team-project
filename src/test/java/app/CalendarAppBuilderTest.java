package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import data_access.CanvasApiDataAccessObject;
import data_access.JsonCalendarDataAccessObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.CalendarView;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarAppBuilderTest {
    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2027-12-31T15:00:00Z"), ZoneId.of("America/Toronto"));

    @TempDir
    Path directory;

    @Test
    void navigatesAcrossYearsSelectsLeapDayAndReopensOffline() throws Exception {
        final HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        final URI origin = URI.create("http://127.0.0.1:" + server.getAddress().getPort());
        final AtomicBoolean offline = new AtomicBoolean();
        server.createContext("/api/v1/courses", exchange -> serve(exchange, offline.get()));
        server.start();
        try {
            final Path file = directory.resolve("deadlines.json");
            final CanvasApiDataAccessObject online =
                    new CanvasApiDataAccessObject(HttpClient.newHttpClient(), origin, "test-token", CLOCK);
            final CalendarView first = open(online, file);
            awaitStatus(first, "Synced");
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(2027, find(first, JSpinner.class).getValue());
                button(first, "Next month").doClick();
            });
            awaitStatus(first, "Synced");
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(2028, find(first, JSpinner.class).getValue());
                button(first, "Next month").doClick();
            });
            awaitStatus(first, "Synced");
            assertLeapDay(first);

            offline.set(true);
            final CalendarView reopened = open(online, file);
            awaitStatus(reopened, "Showing saved deadlines");
            SwingUtilities.invokeAndWait(() -> button(reopened, "Next month").doClick());
            awaitStatus(reopened, "Showing saved deadlines");
            SwingUtilities.invokeAndWait(() -> button(reopened, "Next month").doClick());
            awaitStatus(reopened, "Showing saved deadlines");
            assertLeapDay(reopened);
            SwingUtilities.invokeAndWait(() -> find(reopened, JSpinner.class).setValue(2029));
            awaitStatus(reopened, "Showing saved deadlines");
            SwingUtilities.invokeAndWait(() -> {
                assertEquals(2029, find(reopened, JSpinner.class).getValue());
                assertEquals(28, components(reopened, JButton.class).stream()
                        .filter(cell -> cell.getClientProperty("date") != null).count());
                button(reopened, "Today").doClick();
                assertEquals(2027, find(reopened, JSpinner.class).getValue());
            });
            awaitStatus(reopened, "Showing saved deadlines");
        }
        finally {
            server.stop(0);
        }
    }

    private static CalendarView open(CanvasApiDataAccessObject online, Path file) throws Exception {
        final AtomicReference<CalendarView> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            final CalendarView calendar =
                    new CalendarAppBuilder().build(online, new JsonCalendarDataAccessObject(file), CLOCK);
            calendar.open();
            result.set(calendar);
        });
        return result.get();
    }

    private static void serve(HttpExchange exchange, boolean offline) throws IOException {
        try {
            final String query = exchange.getRequestURI().getRawQuery();
            final String path = exchange.getRequestURI().getPath();
            if (offline || !"Bearer test-token".equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
                exchange.sendResponseHeaders(503, -1);
                return;
            }
            final String json;
            if ("/api/v1/courses".equals(path)) {
                json = "[{\"id\":1,\"name\":\"Software Design\",\"course_code\":\"CSC207\"}]";
            }
            else if (query.contains("page=2")) {
                json = "[{\"id\":3,\"name\":\"Second page\",\"due_at\":\"2028-03-01T03:00:00Z\","
                        + "\"points_possible\":12.5}]";
            }
            else {
                exchange.getResponseHeaders().add("Link", "<?page=2>; rel=\"next\"");
                json = "[{\"id\":2,\"name\":\"First page\",\"due_at\":\"2028-02-29T21:00:00Z\"}]";
            }
            final byte[] body = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
        }
        finally {
            exchange.close();
        }
    }

    private static void assertLeapDay(CalendarView calendar) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            final JButton leapDay = components(calendar, JButton.class).stream()
                    .filter(button -> LocalDate.of(2028, 2, 29).equals(button.getClientProperty("date")))
                    .findFirst().orElseThrow();
            leapDay.doClick();
            final JList<?> details = find(calendar, JList.class);
            assertEquals(2, details.getModel().getSize());
            assertTrue(details.getModel().getElementAt(0).toString().contains("First page"));
            final String second = details.getModel().getElementAt(1).toString();
            assertTrue(second.contains("Second page") && second.contains("12.5 pts") && second.contains("CSC207"));
        });
    }

    private static void awaitStatus(CalendarView calendar, String expected) throws Exception {
        final AtomicBoolean found = new AtomicBoolean();
        final long deadline = System.nanoTime() + java.time.Duration.ofSeconds(6).toNanos();
        while (!found.get() && System.nanoTime() < deadline) {
            SwingUtilities.invokeAndWait(() -> found.set(components(calendar, JLabel.class).stream()
                    .anyMatch(label -> label.getText() != null && label.getText().contains(expected))));
            if (!found.get()) {
                Thread.sleep(20);
            }
        }
        assertTrue(found.get(), "Expected calendar status containing " + expected);
    }

    private static JButton button(CalendarView view, String accessibleName) {
        return components(view, JButton.class).stream()
                .filter(button -> accessibleName.equals(button.getAccessibleContext().getAccessibleName()))
                .findFirst().orElseThrow();
    }

    private static <T extends JComponent> T find(Container root, Class<T> type) {
        return components(root, type).get(0);
    }

    private static <T extends JComponent> List<T> components(Container root, Class<T> type) {
        final List<T> matches = new ArrayList<>();
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) {
                matches.add(type.cast(child));
            }
            if (child instanceof Container) {
                matches.addAll(components((Container) child, type));
            }
        }
        return matches;
    }
}
