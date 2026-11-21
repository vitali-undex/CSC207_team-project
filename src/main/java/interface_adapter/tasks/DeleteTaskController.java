package interface_adapter.tasks;

import use_case.delete_task.DeleteTaskInputBoundary;
import use_case.delete_task.DeleteTaskInputData;

/**
 * The controller for the delete task use case. The tasks tab calls this when
 * the student deletes a task.
 */
public class DeleteTaskController {

    private final DeleteTaskInputBoundary deleteTaskInteractor;

    /**
     * Creates the controller.
     *
     * @param deleteTaskInteractor the delete task use case
     */
    public DeleteTaskController(DeleteTaskInputBoundary deleteTaskInteractor) {
        this.deleteTaskInteractor = deleteTaskInteractor;
    }

    /**
     * Deletes a task.
     *
     * @param taskId the id of the task to delete
     */
    public void execute(String taskId) {
        deleteTaskInteractor.execute(new DeleteTaskInputData(taskId));
    }
}
