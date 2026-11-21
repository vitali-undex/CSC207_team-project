package data_access;

import entity.Task;
import use_case.add_task.AddTaskDataAccessInterface;
import use_case.complete_task.CompleteTaskDataAccessInterface;
import use_case.delete_task.DeleteTaskDataAccessInterface;
import use_case.view_tasks.CourseChoice;
import use_case.view_tasks.ViewTasksDataAccessInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps tasks in memory only, so they are gone when the app closes. Used by the
 * tests, and by the app until tasks are saved to a JSON file.
 */
public class InMemoryTaskDataAccessObject implements AddTaskDataAccessInterface,
        CompleteTaskDataAccessInterface, DeleteTaskDataAccessInterface,
        ViewTasksDataAccessInterface {

    private final Map<String, Task> tasks = new LinkedHashMap<>();
    private final List<CourseChoice> savedCourses;

    /** Creates an empty store with no courses. */
    public InMemoryTaskDataAccessObject() {
        this(Collections.emptyList());
    }

    /**
     * Creates an empty store with the given courses.
     *
     * @param savedCourses the courses a task can be linked to
     */
    public InMemoryTaskDataAccessObject(List<CourseChoice> savedCourses) {
        this.savedCourses = new ArrayList<>(savedCourses);
    }

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
    public void delete(String taskId) {
        tasks.remove(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<CourseChoice> getSavedCourses() {
        return new ArrayList<>(savedCourses);
    }
}
