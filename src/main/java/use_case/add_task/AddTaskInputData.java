package use_case.add_task;

import java.time.LocalDate;

/**
 * What the student entered when adding a task.
 */
public class AddTaskInputData {

    private final String title;
    private final String courseId;
    private final LocalDate dueDate;
    private final boolean pastDueConfirmed;

    /**
     * Creates the input for adding a task.
     *
     * @param title            the title as typed, which may be empty
     * @param courseId         the chosen course's id, or null or empty for none
     * @param dueDate          the chosen due date, or null for none
     * @param pastDueConfirmed true if the student already said yes to adding a
     *                         task whose due date has passed
     */
    public AddTaskInputData(String title, String courseId, LocalDate dueDate,
                            boolean pastDueConfirmed) {
        this.title = title;
        this.courseId = courseId;
        this.dueDate = dueDate;
        this.pastDueConfirmed = pastDueConfirmed;
    }

    public String getTitle() {
        return title;
    }

    public String getCourseId() {
        return courseId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isPastDueConfirmed() {
        return pastDueConfirmed;
    }
}
