package use_case.delete_task;

import use_case.tasks.TaskSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of deleting a task: the deleted task's id and the tasks that are
 * left, sorted by due date.
 */
public class DeleteTaskOutputData {

    private final String deletedTaskId;
    private final List<TaskSummary> tasks;

    /**
     * Creates the output for a task that was deleted.
     *
     * @param deletedTaskId the id of the task that was deleted
     * @param tasks         every task that is left, sorted by due date
     */
    public DeleteTaskOutputData(String deletedTaskId, List<TaskSummary> tasks) {
        this.deletedTaskId = deletedTaskId;
        this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public String getDeletedTaskId() {
        return deletedTaskId;
    }

    public List<TaskSummary> getTasks() {
        return tasks;
    }
}
