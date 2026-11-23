package interface_adapter.tasks;

import interface_adapter.ViewModel;

/**
 * The view model for the "My tasks" tab. All four task use cases update it.
 */
public class TasksViewModel extends ViewModel<TasksState> {

    /** Fired after a task was added, so the view can clear the title field. */
    public static final String TASK_ADDED = "taskAdded";

    /** Fired when the student must confirm a due date that has passed. */
    public static final String PAST_DUE_QUESTION = "pastDueQuestion";

    public static final String VIEW_NAME = "My tasks";
    public static final String TITLE_LABEL = "Title";
    public static final String COURSE_LABEL = "Course";
    public static final String HAS_DUE_DATE_LABEL = "Has due date";
    public static final String ADD_BUTTON_LABEL = "Add task";
    public static final String DELETE_BUTTON_LABEL = "Delete";
    public static final String DONE_COLUMN = "Done";
    public static final String TITLE_COLUMN = "Title";
    public static final String COURSE_COLUMN = "Course";
    public static final String DUE_COLUMN = "Due";
    public static final String PAST_DUE_DIALOG_TITLE = "Due date has passed";
    public static final String INVALID_DUE_DATE_MESSAGE = "Please enter a valid due date.";

    /** Creates the view model with an empty task list. */
    public TasksViewModel() {
        super(VIEW_NAME);
        setState(new TasksState());
    }
}
