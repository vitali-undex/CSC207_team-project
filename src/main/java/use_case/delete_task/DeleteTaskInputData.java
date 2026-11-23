package use_case.delete_task;

/**
 * Which task the student wants to delete.
 */
public class DeleteTaskInputData {

    private final String taskId;

    /**
     * Creates the input for deleting a task.
     *
     * @param taskId the id of the task to delete
     */
    public DeleteTaskInputData(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
