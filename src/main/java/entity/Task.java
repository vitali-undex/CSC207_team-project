package entity;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

/**
 * A task the student keeps for themselves, next to their Quercus deadlines.
 * The course and the due date are optional: a task without them has a null
 * course id or a null due date.
 */

public class Task {
    /**
     * Orders tasks by due date, earliest first. Tasks without a due date come
     * last. Tasks due on the same day are ordered by title, ignoring case.
     */

    public static final Comparator<Task> DUE_DATE_ORDER = Comparator
        .comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
        .thenComparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);

    private final String id;
    private final String title;
    private final String courseId;
    private final LocalDate dueDate;
    private boolean done;

    /**
     * Creates a task.
     *
     * @param id       a unique id for the task, not null
     * @param title    the task's title, not null
     * @param courseId the id of the course it belongs to, or null for none
     * @param dueDate  the day it is due, or null for no due date
     * @param done     whether the task has been ticked off
     */
    public Task(String id, String title, String courseId, LocalDate dueDate, boolean done) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Returns the id of the course this task belongs to.
     *
     * @return the course id, or null if the task is not linked to a course
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Returns the day this task is due.
     *
     * @return the due date, or null if the task has no due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public boolean hasCourse() {
        return courseId != null;
    }

    public boolean hasDueDate() {
        return dueDate != null;
    }

/** For the ticks, we can potentially turn it into a single on-off method */
    /** Ticks the task off. */
    public void complete() {
        done = true;
    }

    /** Unticks the task, for when it was ticked by mistake. */
    public void reopen() {
        done = false;
    }

    /**
     * Returns whether this task is overdue: it is not done and its due date
     * is before the given day. A task due today is not overdue.
     *
     * @param today the current date
     * @return true if the task is overdue
     */
    public boolean isOverdue(LocalDate today) {
        return !done && dueDate != null && dueDate.isBefore(today);
    }
}
