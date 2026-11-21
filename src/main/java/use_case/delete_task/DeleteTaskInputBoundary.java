package use_case.delete_task;

/**
 * The input boundary for the delete task use case. The delete task controller
 * calls this.
 */
public interface DeleteTaskInputBoundary {

    /**
     * Deletes a task.
     *
     * @param inputData which task to delete
     */
    void execute(DeleteTaskInputData inputData);
}
