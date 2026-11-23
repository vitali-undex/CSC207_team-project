package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Standalone launcher for the announcements use case (use case 4). */
public final class AnnouncementsMain {
    private AnnouncementsMain() {
    }

    /** Opens the announcements window. */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = AnnouncementsAppBuilder.build();
            frame.setVisible(true);
        });
    }
}
