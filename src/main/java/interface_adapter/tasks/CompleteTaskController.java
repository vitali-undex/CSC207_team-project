package interface_adapter.tasks;

import use_case.complete_task.CompleteTaskInputBoundary;
import use_case.complete_task.CompleteTaskInputData;

/**
 * The controller for the complete task use case. The tasks tab calls this
 * when the student ticks or unticks a task.
 */
public class CompleteTaskController {

    private final CompleteTaskInputBoundary completeTaskInteractor;

    /**
     * Creates the controller.
     *
     * @param completeTaskInteractor the complete task use case
     */
    public CompleteTaskController(CompleteTaskInputBoundary completeTaskInteractor) {
        this.completeTaskInteractor = completeTaskInteractor;
    }

    /**
     * Ticks or unticks a task.
     *
     * @param taskId the id of the task
     * @param done   true to tick it off, false to untick it
     */
    public void execute(String taskId, boolean done) {
        completeTaskInteractor.execute(new CompleteTaskInputData(taskId, done));
    }
}
