package use_case.add_task;

import use_case.tasks.TaskSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of adding a task: the new task's id and the whole task list,
 * sorted by due date.
 */
public class AddTaskOutputData {

    private final String addedTaskId;
    private final List<TaskSummary> tasks;

    /**
     * Creates the output for a task that was added.
     *
     * @param addedTaskId the id of the task that was just added
     * @param tasks       every task, sorted by due date
     */
    public AddTaskOutputData(String addedTaskId, List<TaskSummary> tasks) {
        this.addedTaskId = addedTaskId;
        this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public String getAddedTaskId() {
        return addedTaskId;
    }

    public List<TaskSummary> getTasks() {
        return tasks;
    }
}
