package use_case.add_task;

import entity.Task;

import java.util.List;

/**
 * The storage the add task use case needs. 
 * This may change depending on how we store data in the future.
 */
public interface AddTaskDataAccessInterface {

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
