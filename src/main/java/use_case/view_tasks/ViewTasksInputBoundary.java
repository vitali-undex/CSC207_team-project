package use_case.view_tasks;

/**
 * The input boundary for the view tasks use case. The view tasks controller
 * calls this when the tasks tab opens.
 */
public interface ViewTasksInputBoundary {

    /** Loads the saved tasks and the courses they can be linked to. */
    void execute();
}
