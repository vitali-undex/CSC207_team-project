package use_case.tasks;

import entity.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sorts tasks and turns them into summaries for the presenter. The add task,
 * complete task and view tasks interactors all use this, so the task list is
 * always shown in the same order.
 */
public class TaskListMapper {

    /**
     * Returns summaries of the given tasks, sorted by due date as in
     * {@link Task#DUE_DATE_ORDER}. The given list is not changed.
     *
     * @param tasks the tasks to summarize
     * @param today the current date, used to work out which tasks are overdue
     * @return the sorted summaries
     */
    public List<TaskSummary> toSortedSummaries(List<Task> tasks, LocalDate today) {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Task.DUE_DATE_ORDER);

        List<TaskSummary> summaries = new ArrayList<>();
        for (Task task : sorted) {
            summaries.add(new TaskSummary(task.getId(), task.getTitle(), task.getCourseId(),
                    task.getDueDate(), task.isDone(), task.isOverdue(today)));
        }
        return summaries;
    }
}
