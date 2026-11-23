package view;

import interface_adapter.tasks.AddTaskController;
import interface_adapter.tasks.TasksViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import use_case.add_task.AddTaskInputData;

import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests due-date validation before a task is sent to the controller. */
class TasksViewTest {

    private final List<AddTaskInputData> requests = new ArrayList<>();
    private TasksViewModel viewModel;
    private JTextField titleField;
    private JCheckBox hasDueDateBox;
    private JSpinner.DateEditor dateEditor;

    @BeforeEach
    void setUp() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            viewModel = new TasksViewModel();
            final TasksView view = new TasksView(viewModel);
            view.setAddTaskController(new AddTaskController(requests::add));
            titleField = findComponent(view, JTextField.class);
            hasDueDateBox = findComponent(view, JCheckBox.class);
            dateEditor = (JSpinner.DateEditor) findComponent(view, JSpinner.class).getEditor();
            titleField.setText("Read chapter 3");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"not a date", ""})
    void invalidDueDateDoesNotSubmitThePreviousDate(String text)
            throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            hasDueDateBox.doClick();
            dateEditor.getTextField().setText(text);

            titleField.postActionEvent();

            assertTrue(requests.isEmpty());
            assertEquals("Please enter a valid due date.", viewModel.getState().getErrorMessage());
            assertEquals("Read chapter 3", titleField.getText());
        });
    }

    @Test
    void impossibleDateIsNotChangedToAnotherDay()
            throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            hasDueDateBox.doClick();
            final String validDate = dateEditor.getFormat().format(toDate(LocalDate.of(2028, 2, 28)));
            dateEditor.getTextField().setText(validDate.replace("28,", "30,"));

            titleField.postActionEvent();

            assertTrue(requests.isEmpty());
            assertEquals("Please enter a valid due date.", viewModel.getState().getErrorMessage());
        });
    }

    @Test
    void correctedDateIsSubmitted()
            throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            hasDueDateBox.doClick();
            dateEditor.getTextField().setText("not a date");
            titleField.postActionEvent();
            final LocalDate dueDate = LocalDate.of(2028, 2, 28);
            dateEditor.getTextField().setText(dateEditor.getFormat().format(toDate(dueDate)));

            titleField.postActionEvent();

            assertEquals(1, requests.size());
            assertEquals(dueDate, requests.get(0).getDueDate());
            assertEquals("Read chapter 3", requests.get(0).getTitle());
        });
    }

    @Test
    void uncheckedDueDateIgnoresInvalidDateText()
            throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            dateEditor.getTextField().setText("not a date");

            titleField.postActionEvent();

            assertEquals(1, requests.size());
            assertNull(requests.get(0).getDueDate());
        });
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static <T extends Component> T findComponent(Container parent, Class<T> type) {
        for (Component component : parent.getComponents()) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
            if (component instanceof Container) {
                final T nested = findComponent((Container) component, type);
                if (nested != null) {
                    return nested;
                }
            }
        }
        return null;
    }
}
