package interface_adapter.tasks;

import use_case.add_task.AddTaskInputBoundary;
import use_case.add_task.AddTaskInputData;

import java.time.LocalDate;

/**
 * The controller for the add task use case. The tasks tab calls this when
 * the student adds a task.
 */
public class AddTaskController {

    private final AddTaskInputBoundary addTaskInteractor;

    /**
     * Creates the controller.
     *
     * @param addTaskInteractor the add task use case
     */
    public AddTaskController(AddTaskInputBoundary addTaskInteractor) {
        this.addTaskInteractor = addTaskInteractor;
    }

    /**
     * Adds a task.
     *
     * @param title            the title as typed
     * @param courseId         the chosen course's id, or null for none
     * @param dueDate          the chosen due date, or null for none
     * @param pastDueConfirmed true if the student already said yes to a due date that has passed
     */
    public void execute(String title, String courseId, LocalDate dueDate,
                        boolean pastDueConfirmed) {
        addTaskInteractor.execute(new AddTaskInputData(title, courseId, dueDate, pastDueConfirmed));
    }
}
