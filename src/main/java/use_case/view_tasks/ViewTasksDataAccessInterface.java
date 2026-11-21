package use_case.view_tasks;

import entity.Task;

import java.util.List;

/**
 * The storage the view tasks use case needs.
 */
public interface ViewTasksDataAccessInterface {

    /**
     * Returns every saved task, in no particular order.
     *
     * @return the saved tasks
     */
    List<Task> getAllTasks();

    /**
     * Returns the courses a task can be linked to.
     *
     * @return the saved courses, or an empty list if there are none
     */
    List<CourseChoice> getSavedCourses();
}
