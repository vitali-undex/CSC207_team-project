package interface_adapter.tasks;

/**
 * One row of the task table, already formatted for display.
 */
public class TaskRow {

    private final String id;
    private final boolean done;
    private final String title;
    private final String course;
    private final String due;
    private final boolean overdue;

    /**
     * Creates a row.
     *
     * @param id      the task's id, which the view sends back when a row is ticked or deleted
     * @param done    whether the task is ticked off
     * @param title   the task's title
     * @param course  the course code, or an empty string for no course
     * @param due     the formatted due date, or an empty string for no due date
     * @param overdue whether the row should be shown as overdue
     */
    public TaskRow(String id, boolean done, String title, String course, String due,
                   boolean overdue) {
        this.id = id;
        this.done = done;
        this.title = title;
        this.course = course;
        this.due = due;
        this.overdue = overdue;
    }

    public String getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public String getTitle() {
        return title;
    }

    public String getCourse() {
        return course;
    }

    public String getDue() {
        return due;
    }

    public boolean isOverdue() {
        return overdue;
    }
}
