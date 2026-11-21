package interface_adapter.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What the tasks tab shows: the task rows, the course menu, an error message,
 * and the question to ask when a due date has passed.
 */
public class TasksState {

    private List<TaskRow> rows = Collections.emptyList();
    private List<CourseOption> courseOptions = Collections.singletonList(CourseOption.NONE);
    private String errorMessage = "";
    private String pastDueQuestion = "";

    public List<TaskRow> getRows() {
        return rows;
    }

    public void setRows(List<TaskRow> rows) {
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }

    /**
     * Returns the entries of the course menu. The first one is always
     * {@link CourseOption#NONE}.
     *
     * @return the course menu entries
     */
    public List<CourseOption> getCourseOptions() {
        return courseOptions;
    }

    public void setCourseOptions(List<CourseOption> courseOptions) {
        this.courseOptions = Collections.unmodifiableList(new ArrayList<>(courseOptions));
    }

    /**
     * Returns the error to show under the form.
     *
     * @return the error message, or an empty string if there is none
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the question to ask before adding a task whose due date has
     * passed (extension 1a).
     *
     * @return the question, or an empty string if there is none
     */
    public String getPastDueQuestion() {
        return pastDueQuestion;
    }

    public void setPastDueQuestion(String pastDueQuestion) {
        this.pastDueQuestion = pastDueQuestion;
    }
}
