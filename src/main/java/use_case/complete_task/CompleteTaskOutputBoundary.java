package use_case.complete_task;

/**
 * The output boundary for the complete task use case. The complete task
 * presenter implements this.
 */
public interface CompleteTaskOutputBoundary {

    /**
     * Shows the updated task list after a task was ticked or unticked.
     *
     * @param outputData the changed task and the sorted task list
     */
    void prepareSuccessView(CompleteTaskOutputData outputData);

    /**
     * Shows why the task could not be changed. Nothing was saved.
     *
     * @param errorMessage the message to show the student
     */
    void prepareFailView(String errorMessage);
}
