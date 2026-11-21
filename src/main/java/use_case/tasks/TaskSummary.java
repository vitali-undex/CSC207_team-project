package use_case.tasks;

import java.time.LocalDate;

/**
 * What the task use cases tell the presenter about one task. Presenters get
 * these instead of Task entities, so the outer layers never change an entity.
 */
public class TaskSummary {
    private final String id;
    private final String title;
    private final String courseId;
    private final LocalDate dueDate;
    private final boolean done;
    private final boolean overdue;

    /**
     * Creates a summary of one task.
     *
     * @param id       the task's id
     * @param title    the task's title
     * @param courseId the id of the task's course, or null for none
     * @param dueDate  the task's due date, or null for none
     * @param done     whether the task is ticked off
     * @param overdue  whether the task is overdue today
     */
    public TaskSummary(String id, String title, String courseId, LocalDate dueDate,
                       boolean done, boolean overdue) {
        this.id = id;
        this.title = title;
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.done = done;
        this.overdue = overdue;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Returns the id of the task's course. The view turns it into a course
     * code using the course list it already has.
     *
     * @return the course id, or null if the task is not linked to a course
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Returns the task's due date.
     *
     * @return the due date, or null if the task has no due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isOverdue() {
        return overdue;
    }
}
