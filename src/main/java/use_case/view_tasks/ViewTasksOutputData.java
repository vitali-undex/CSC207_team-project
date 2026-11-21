package use_case.view_tasks;

import use_case.tasks.TaskSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What the tasks tab shows when it opens: every task, sorted by due date, and
 * the courses a new task can be linked to.
 */
public class ViewTasksOutputData {

    private final List<TaskSummary> tasks;
    private final List<CourseChoice> courses;

    /**
     * Creates the output for opening the tasks tab.
     *
     * @param tasks   every task, sorted by due date
     * @param courses the courses a task can be linked to
     */
    public ViewTasksOutputData(List<TaskSummary> tasks, List<CourseChoice> courses) {
        this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
        this.courses = Collections.unmodifiableList(new ArrayList<>(courses));
    }

    public List<TaskSummary> getTasks() {
        return tasks;
    }

    public List<CourseChoice> getCourses() {
        return courses;
    }
}
