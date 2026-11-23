package use_case.delete_task;

import entity.Task;

import java.util.List;

/**
 * The storage the delete task use case needs.
 */
public interface DeleteTaskDataAccessInterface {

    /**
     * Returns whether a task with the given id is saved.
     *
     * @param taskId the id to look for
     * @return true if a task with that id is saved
     */
    boolean existsById(String taskId);

    /**
     * Deletes the saved task with the given id. Does nothing if there is none.
     *
     * @param taskId the id of the task to delete
     */
    void delete(String taskId);

    /**
     * Returns every saved task, in no particular order.
     *
     * @return the saved tasks
     */
    List<Task> getAllTasks();
}
