package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Starts the app.
 */
public class Main {

    /**
     * Builds the main window and shows it.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            useSystemLookAndFeel();

            final AppBuilder appBuilder = new AppBuilder();
            final JFrame application = appBuilder
                    .addTasksView()
                    .addAddTaskUseCase()
                    .addCompleteTaskUseCase()
                    .addDeleteTaskUseCase()
                    .addViewTasksUseCase()
                    .build();

            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }

    /** Makes the window look like the rest of the operating system, if it can. */
    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ReflectiveOperationException | UnsupportedLookAndFeelException exception) {
            // Keep Swing's default look.
        }
    }
}
