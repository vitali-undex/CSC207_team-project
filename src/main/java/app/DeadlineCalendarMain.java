package app;

import data_access.CanvasApiDataAccessObject;
import data_access.JsonCalendarDataAccessObject;
import view.CalendarView;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Locale;

/** Standalone entry point until the shared account and main window are integrated. */
public final class DeadlineCalendarMain {
    private DeadlineCalendarMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeadlineCalendarMain::openWindow);
    }

    private static void openWindow() {
        final String token = System.getenv("CANVAS_API_TOKEN");
        if (token == null || token.isBlank()) {
            JOptionPane.showMessageDialog(null, "Set CANVAS_API_TOKEN before starting the deadline calendar.",
                    "Quercus Calendar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final URI origin;
        try {
            origin = canvasOrigin(System.getenv().getOrDefault("CANVAS_BASE_URL", "https://q.utoronto.ca"));
        }
        catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(null, "CANVAS_BASE_URL must be an HTTPS Canvas server address.",
                    "Quercus Calendar", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Clock clock = Clock.system(ZoneId.of("America/Toronto"));
        final HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        final CanvasApiDataAccessObject online = new CanvasApiDataAccessObject(client, origin, token, clock);
        final Path cacheFile = Path.of(System.getProperty("user.home"), ".quercus-tracker",
                "deadlines-" + cacheKey(origin, token) + ".json");
        final CalendarView calendar =
                new CalendarAppBuilder().build(online, new JsonCalendarDataAccessObject(cacheFile), clock);
        final JFrame frame = new JFrame("Quercus Course Tracker · Calendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(calendar);
        frame.setMinimumSize(new Dimension(950, 630));
        frame.setSize(1240, 770);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        calendar.open();
    }

    private static URI canvasOrigin(String address) {
        final URI uri = URI.create(address);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null || uri.getUserInfo() != null
                || uri.getQuery() != null || uri.getFragment() != null
                || (!uri.getPath().isEmpty() && !"/".equals(uri.getPath()))) {
            throw new IllegalArgumentException("Expected an HTTPS origin");
        }
        final String host = uri.getHost().toLowerCase(Locale.ROOT);
        return URI.create("https://" + host + (uri.getPort() == -1 || uri.getPort() == 443 ? "" : ":" + uri.getPort()));
    }

    private static String cacheKey(URI origin, String token) {
        try {
            final byte[] identity = (origin + "\n" + token).getBytes(StandardCharsets.UTF_8);
            final byte[] digest = MessageDigest.getInstance("SHA-256").digest(identity);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        }
        catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
