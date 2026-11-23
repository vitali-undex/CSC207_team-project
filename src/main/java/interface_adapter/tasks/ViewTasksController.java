package interface_adapter.tasks;

import use_case.view_tasks.ViewTasksInputBoundary;

/**
 * The controller for the view tasks use case. Called when the tasks tab opens.
 */
public class ViewTasksController {

    private final ViewTasksInputBoundary viewTasksInteractor;

    /**
     * Creates the controller.
     *
     * @param viewTasksInteractor the view tasks use case
     */
    public ViewTasksController(ViewTasksInputBoundary viewTasksInteractor) {
        this.viewTasksInteractor = viewTasksInteractor;
    }

    /** Loads the saved tasks and the course choices. */
    public void execute() {
        viewTasksInteractor.execute();
    }
}
