package view;

import app.GradeCalculatorAppBuilder;
import data_access.DemoGradeDataAccessObject;
import data_access.JsonGradingSchemeDataAccessObject;
import interface_adapter.calculate_grade.Choice;
import interface_adapter.calculate_grade.GradeCalculatorPresenter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import use_case.calculate_grade.CalculateGradeInteractor;
import use_case.calculate_grade.CourseOption;
import use_case.calculate_grade.GradeSourceDataAccessInterface;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static view.GradeCalculatorView.AVERAGE_COLUMN;
import static view.GradeCalculatorView.AVERAGING_COLUMN;
import static view.GradeCalculatorView.DROP_COLUMN;
import static view.GradeCalculatorView.GRADED_COLUMN;
import static view.GradeCalculatorView.GROUP_COLUMN;
import static view.GradeCalculatorView.WEIGHT_COLUMN;

/** Drives the real screen with the demo data, the way a student would. */
class GradeCalculatorViewTest {

    @TempDir
    Path folder;

    private GradeCalculatorView openView(GradeSourceDataAccessInterface source) {
        GradeCalculatorView view = new GradeCalculatorAppBuilder()
                .build(source, new JsonGradingSchemeDataAccessObject(folder.resolve("grading-schemes.json")));
        view.open();
        return view;
    }

    private GradeCalculatorView openDemo() {
        return openView(new DemoGradeDataAccessObject());
    }

    private static void typeWeights(GradeCalculatorView view, String... weights) {
        for (int row = 0; row < weights.length; row++) {
            view.getTable().setValueAt(weights[row], row, WEIGHT_COLUMN);
        }
    }

    private static void onSwingThread(Runnable test) throws Throwable {
        try {
            SwingUtilities.invokeAndWait(test);
        }
        catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }

    @Test
    void opensOnTheFirstCourseReadyForWeights() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openDemo();

            assertEquals(3, view.getCourseBox().getItemCount());
            assertEquals("DEMO101 · Introduction to Demo Studies", view.getCourseBox().getSelectedItem().toString());
            JTable table = view.getTable();
            assertEquals(4, table.getRowCount());
            assertEquals("Quizzes", table.getValueAt(0, GROUP_COLUMN));
            assertEquals("0", table.getValueAt(0, WEIGHT_COLUMN));
            assertEquals("0", table.getValueAt(0, DROP_COLUMN));
            assertEquals("By points", table.getValueAt(0, AVERAGING_COLUMN).toString());
            assertEquals("86.67%", table.getValueAt(0, AVERAGE_COLUMN));
            assertEquals("4 of 5", table.getValueAt(0, GRADED_COLUMN));
            assertEquals(GradeCalculatorPresenter.NO_AVERAGE, table.getValueAt(3, AVERAGE_COLUMN));
            assertTrue(table.isCellEditable(0, WEIGHT_COLUMN));
            assertTrue(table.isCellEditable(0, DROP_COLUMN));
            assertTrue(table.isCellEditable(0, AVERAGING_COLUMN));
            assertFalse(table.isCellEditable(0, GROUP_COLUMN));
            assertFalse(table.isCellEditable(0, AVERAGE_COLUMN));
            assertTrue(view.getCalculateButton().isEnabled());
            assertEquals(GradeCalculatorPresenter.ENTER_WEIGHTS_MESSAGE, view.getDetailLabel().getText());
        });
    }

    @Test
    void calculatesTheGradeAndRemembersTheSchemeAfterReopening() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openDemo();
            typeWeights(view, "20", "30", "20", "30");
            view.getCalculateButton().doClick();

            assertEquals("Current grade: 81.33%", view.getGradeLabel().getText());
            assertEquals("Based on 70% of the course graded so far. Weights saved.", view.getDetailLabel().getText());

            JTable table = view.getTable();
            table.setValueAt(new Choice("equal", "Equally"), 0, AVERAGING_COLUMN);
            view.getCalculateButton().doClick();
            assertEquals("Current grade: 80.14%", view.getGradeLabel().getText());

            table.setValueAt("1", 0, DROP_COLUMN);
            view.getCalculateButton().doClick();
            assertEquals("Current grade: 82.29%", view.getGradeLabel().getText());
            assertEquals("90%", table.getValueAt(0, AVERAGE_COLUMN));

            GradeCalculatorView reopened = openDemo();
            JTable reopenedTable = reopened.getTable();
            assertEquals("20", reopenedTable.getValueAt(0, WEIGHT_COLUMN));
            assertEquals("1", reopenedTable.getValueAt(0, DROP_COLUMN));
            assertEquals("Equally", reopenedTable.getValueAt(0, AVERAGING_COLUMN).toString());
            assertEquals("Current grade: 82.29%", reopened.getGradeLabel().getText());
            assertEquals("Based on 70% of the course graded so far. Using the weights you saved.",
                    reopened.getDetailLabel().getText());
        });
    }

    @Test
    void explainsWhatIsWrongAndKeepsWhatWasTyped() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openDemo();
            typeWeights(view, "20", "30", "20", "25");
            view.getCalculateButton().doClick();

            assertEquals("The weights add up to 95%. They need to add up to 100% (5% missing).",
                    view.getErrorLabel().getText());
            assertEquals(" ", view.getGradeLabel().getText());
            assertEquals("25", view.getTable().getValueAt(3, WEIGHT_COLUMN));
        });
    }

    @Test
    void finishesAnEditInProgressBeforeCalculating() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openDemo();
            typeWeights(view, "20", "30", "20", "0");
            JTable table = view.getTable();
            assertTrue(table.editCellAt(3, WEIGHT_COLUMN));
            ((JTextField) table.getEditorComponent()).setText("30");

            view.getCalculateButton().doClick();

            assertFalse(table.isEditing());
            assertEquals("Current grade: 81.33%", view.getGradeLabel().getText());
        });
    }

    @Test
    void pickingAnotherCourseShowsThatCourse() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openDemo();
            view.getTable().editCellAt(0, WEIGHT_COLUMN);

            view.getCourseBox().setSelectedItem(null);
            assertEquals(4, view.getTable().getRowCount());

            view.getCourseBox().setSelectedIndex(2);

            JTable table = view.getTable();
            assertFalse(table.isEditing());
            assertEquals(3, table.getRowCount());
            assertEquals("Problem sets", table.getValueAt(0, GROUP_COLUMN));
            typeWeights(view, "30", "30", "40");
            view.getCalculateButton().doClick();
            assertEquals("No grade yet", view.getGradeLabel().getText());
            assertEquals("Nothing with a weight has been graded yet. Weights saved.", view.getDetailLabel().getText());
        });
    }

    @Test
    void saysSoWhenThereAreNoCourses() throws Throwable {
        onSwingThread(() -> {
            GradeCalculatorView view = openView(new GradeSourceDataAccessInterface() {
                @Override
                public List<CourseOption> getCourses() {
                    return List.of();
                }

                @Override
                public entity.Gradebook getGradebook(String courseId) throws IOException {
                    throw new IOException("no courses");
                }
            });

            assertEquals(CalculateGradeInteractor.NO_COURSES_MESSAGE, view.getErrorLabel().getText());
            assertEquals(0, view.getTable().getRowCount());
            assertFalse(view.getCalculateButton().isEnabled());
        });
    }
}
