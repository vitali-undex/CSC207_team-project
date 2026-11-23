package app;

import data_access.DemoGradeDataAccessObject;
import data_access.JsonGradingSchemeDataAccessObject;
import view.GradeCalculatorView;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.nio.file.Path;

/**
 * Opens the grade calculator on its own, with made-up demo courses, until it
 * joins the main window and the connect Quercus use case is in place.
 */
public final class GradeCalculatorMain {

    private static final int WIDTH = 960;
    private static final int HEIGHT = 640;
    private static final int PADDING = 20;

    private GradeCalculatorMain() {
    }

    /**
     * Opens the grade calculator window.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeCalculatorMain::openWindow);
    }

    /**
     * Returns where grading schemes are saved: a file in the student's home
     * folder, never in the project.
     *
     * @return the path of the grading scheme file
     */
    static Path schemeFile() {
        return Path.of(System.getProperty("user.home"), ".quercus-tracker", "grading-schemes.json");
    }

    private static void openWindow() {
        final GradeCalculatorView view = new GradeCalculatorAppBuilder().build(
                new DemoGradeDataAccessObject(),
                new JsonGradingSchemeDataAccessObject(schemeFile()));

        final JLabel banner = new JLabel("Demo data: these courses and marks are made up. "
                + "Weights are saved in ~/.quercus-tracker/grading-schemes.json.");
        banner.setForeground(new Color(0x495E76));
        banner.setBorder(BorderFactory.createEmptyBorder(PADDING / 2, PADDING, 0, PADDING));

        final JFrame frame = new JFrame("Quercus Course Tracker · Grades (demo data)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(banner, BorderLayout.NORTH);
        frame.getContentPane().add(view, BorderLayout.CENTER);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        view.open();
    }
}
