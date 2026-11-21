package use_case.complete_task;

/**
 * Which task the student ticked or unticked.
 */
public class CompleteTaskInputData {

    private final String taskId;
    private final boolean done;

    /**
     * Creates the input for ticking or unticking a task.
     *
     * @param taskId the id of the task
     * @param done   true to tick the task off, false to untick it
     */
    public CompleteTaskInputData(String taskId, boolean done) {
        this.taskId = taskId;
        this.done = done;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isDone() {
        return done;
    }
}
