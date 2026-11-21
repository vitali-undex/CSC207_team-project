package use_case.complete_task;

import entity.Task;

import java.util.List;

/**
 * The storage the complete task use case needs.
 */
public interface CompleteTaskDataAccessInterface {

    /**
     * Returns whether a task with the given id is saved.
     *
     * @param taskId the id to look for
     * @return true if a task with that id is saved
     */
    boolean existsById(String taskId);

    /**
     * Returns the saved task with the given id. Call {@link #existsById} first.
     *
     * @param taskId the id of a saved task
     * @return the task with that id
     */
    Task getTask(String taskId);

    /**
     * Saves a task. If a task with the same id is already saved, it is replaced.
     *
     * @param task the task to save
     */
    void save(Task task);

    /**
     * Returns every saved task, in no particular order.
     *
     * @return the saved tasks
     */
    List<Task> getAllTasks();
}
