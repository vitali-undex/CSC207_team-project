package use_case.view_tasks;

/**
 * The output boundary for the view tasks use case. The view tasks presenter
 * implements this.
 */
public interface ViewTasksOutputBoundary {

    /**
     * Shows the saved tasks and the course choices.
     *
     * @param outputData the sorted task list and the course choices
     */
    void prepareSuccessView(ViewTasksOutputData outputData);
}
