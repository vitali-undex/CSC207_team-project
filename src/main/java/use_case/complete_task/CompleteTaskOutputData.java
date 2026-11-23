package use_case.complete_task;

import use_case.tasks.TaskSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of ticking or unticking a task: which task changed, whether it
 * is now done, and the whole task list, sorted by due date.
 */
public class CompleteTaskOutputData {

    private final String taskId;
    private final boolean done;
    private final List<TaskSummary> tasks;

    /**
     * Creates the output for a task that was ticked or unticked.
     *
     * @param taskId the id of the task that changed
     * @param done   whether the task is now done
     * @param tasks  every task, sorted by due date
     */
    public CompleteTaskOutputData(String taskId, boolean done, List<TaskSummary> tasks) {
        this.taskId = taskId;
        this.done = done;
        this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isDone() {
        return done;
    }

    public List<TaskSummary> getTasks() {
        return tasks;
    }
}
