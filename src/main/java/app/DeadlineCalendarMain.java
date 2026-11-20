package app;

import data_access.CanvasApiDataAccessObject;
import data_access.JsonCalendarDataAccessObject;
import view.CalendarView;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Arrays;
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
        final Clock clock = Clock.system(ZoneId.of("America/Toronto"));
        final CalendarAppBuilder builder = new CalendarAppBuilder();
        final JFrame frame = new JFrame("Quercus Course Tracker · Calendar");
        final JPanel content = new JPanel(new BorderLayout());
        final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 22, 0, 22));
        final JButton connect = new JButton("Connect Quercus");
        toolbar.add(connect);
        content.add(toolbar, BorderLayout.NORTH);
        content.add(builder.buildPreview(clock), BorderLayout.CENTER);
        connect.addActionListener(event -> {
            final String token = promptForToken(frame);
            if (token != null && !token.isBlank()) {
                connectCalendar(frame, content, clock, builder, token);
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(content);
        frame.setMinimumSize(new Dimension(950, 630));
        frame.setSize(1240, 770);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final String configuredToken = System.getenv("CANVAS_API_TOKEN");
        if (configuredToken != null && !configuredToken.isBlank()) {
            SwingUtilities.invokeLater(() -> connectCalendar(frame, content, clock, builder, configuredToken.strip()));
        }
    }

    private static void connectCalendar(JFrame frame, JPanel content, Clock clock,
                                        CalendarAppBuilder builder, String token) {
        final URI origin;
        try {
            origin = canvasOrigin(System.getenv().getOrDefault("CANVAS_BASE_URL", "https://q.utoronto.ca"));
        }
        catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(frame, "CANVAS_BASE_URL must be an HTTPS Canvas server address.",
                    "Quercus Calendar", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        final CanvasApiDataAccessObject online = new CanvasApiDataAccessObject(client, origin, token, clock);
        final Path cacheFile = Path.of(System.getProperty("user.home"), ".quercus-tracker",
                "deadlines-" + cacheKey(origin, token) + ".json");
        final CalendarView calendar =
                builder.build(online, new JsonCalendarDataAccessObject(cacheFile), clock);
        final BorderLayout layout = (BorderLayout) content.getLayout();
        final CalendarView previous = (CalendarView) layout.getLayoutComponent(BorderLayout.CENTER);
        content.remove(previous);
        content.add(calendar, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
        calendar.open(previous.getSelectedDate());
    }

    private static String promptForToken(JFrame frame) {
        final JPasswordField field = new JPasswordField(28);
        field.getAccessibleContext().setAccessibleName("Quercus access token");
        final int answer = JOptionPane.showConfirmDialog(frame,
                new Object[]{"Paste your Quercus personal access token.", field},
                "Connect Quercus", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        final char[] password = field.getPassword();
        try {
            return answer == JOptionPane.OK_OPTION ? new String(password).strip() : null;
        }
        finally {
            Arrays.fill(password, '\0');
            field.setText("");
        }
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
