package data_access;

import entity.Task;
import use_case.add_task.AddTaskDataAccessInterface;
import use_case.complete_task.CompleteTaskDataAccessInterface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps tasks in memory only, so they are gone when the app closes. Used by the
 * tests, and by the app until tasks are saved to a JSON file.
 */
public class InMemoryTaskDataAccessObject implements AddTaskDataAccessInterface,
        CompleteTaskDataAccessInterface {

    private final Map<String, Task> tasks = new LinkedHashMap<>();

    @Override
    public boolean existsById(String taskId) {
        return tasks.containsKey(taskId);
    }

    @Override
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public void save(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
}
