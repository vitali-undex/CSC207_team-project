package interface_adapter.tasks;

import use_case.tasks.TaskSummary;
import use_case.view_tasks.CourseChoice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Turns what the task use cases return into what the tasks tab shows. All
 * four task presenters use this, so dates and course codes look the same
 * everywhere.
 */
public class TaskRowFormatter {

    /** How due dates are shown, for example "Fri, Nov 20, 2026". */
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH);

    /**
     * Turns task summaries into table rows, in the same order.
     *
     * @param tasks         the tasks, already sorted
     * @param courseOptions the course menu entries, used to show a course code for each task
     * @return one row per task
     */
    public List<TaskRow> toRows(List<TaskSummary> tasks, List<CourseOption> courseOptions) {
        final Map<String, String> courseLabels = new HashMap<>();
        for (CourseOption option : courseOptions) {
            if (option.getCourseId() != null) {
                courseLabels.put(option.getCourseId(), option.getLabel());
            }
        }

        final List<TaskRow> rows = new ArrayList<>();
        for (TaskSummary task : tasks) {
            rows.add(new TaskRow(task.getId(), task.isDone(), task.getTitle(),
                    courseLabel(task.getCourseId(), courseLabels), formatDate(task.getDueDate()),
                    task.isOverdue()));
        }
        return rows;
    }

    /**
     * Turns course choices into course menu entries, with "None" first.
     *
     * @param courses the courses a task can be linked to
     * @return the menu entries
     */
    public List<CourseOption> toCourseOptions(List<CourseChoice> courses) {
        final List<CourseOption> options = new ArrayList<>();
        options.add(CourseOption.NONE);
        for (CourseChoice course : courses) {
            options.add(new CourseOption(course.getId(), course.getCode()));
        }
        return options;
    }

    /**
     * Formats a due date for display.
     *
     * @param date the date, or null
     * @return the formatted date, or an empty string for null
     */
    public String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }

    private static String courseLabel(String courseId, Map<String, String> courseLabels) {
        if (courseId == null) {
            return "";
        }
        return courseLabels.getOrDefault(courseId, courseId);
    }
}
