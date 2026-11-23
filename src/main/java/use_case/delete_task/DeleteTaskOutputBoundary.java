package use_case.delete_task;

/**
 * The output boundary for the delete task use case. The delete task presenter
 * implements this.
 */
public interface DeleteTaskOutputBoundary {

    /**
     * Shows the updated task list after a task was deleted.
     *
     * @param outputData the deleted task's id and the sorted task list
     */
    void prepareSuccessView(DeleteTaskOutputData outputData);

    /**
     * Shows why the task could not be deleted. Nothing was changed.
     *
     * @param errorMessage the message to show the student
     */
    void prepareFailView(String errorMessage);
}
