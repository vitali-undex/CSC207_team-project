package use_case.complete_task;

import entity.Task;
import use_case.tasks.TaskListMapper;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * The complete task use case (use case 5, step 5 and extension 5a).
 *
 * <ul>
 *   <li>Ticking a task marks it as done and saves it.</li>
 *   <li>Unticking a task marks it as not done and saves it (extension 5a).</li>
 *   <li>If the task no longer exists, nothing is saved.</li>
 * </ul>
 */
public class CompleteTaskInteractor implements CompleteTaskInputBoundary {

    /** Shown when the task to tick or untick is not saved. */
    public static final String UNKNOWN_TASK_MESSAGE = "That task no longer exists.";

    private final CompleteTaskDataAccessInterface taskDataAccessObject;
    private final CompleteTaskOutputBoundary presenter;
    private final Clock clock;
    private final TaskListMapper taskListMapper = new TaskListMapper();

    /**
     * Creates the interactor.
     *
     * @param taskDataAccessObject where tasks are saved
     * @param presenter            receives the result
     * @param clock                gives today's date; tests pass a fixed clock
     */
    public CompleteTaskInteractor(CompleteTaskDataAccessInterface taskDataAccessObject,
                                  CompleteTaskOutputBoundary presenter,
                                  Clock clock) {
        this.taskDataAccessObject = taskDataAccessObject;
        this.presenter = presenter;
        this.clock = clock;
    }

    @Override
    public void execute(CompleteTaskInputData inputData) {
        final String taskId = inputData.getTaskId();

        if (!taskDataAccessObject.existsById(taskId)) {
            presenter.prepareFailView(UNKNOWN_TASK_MESSAGE);
        }
        else {
            final Task task = taskDataAccessObject.getTask(taskId);
            if (inputData.isDone()) {
                task.complete();
            }
            else {
                task.reopen();
            }
            taskDataAccessObject.save(task);

            final List<TaskSummary> tasks = taskListMapper.toSortedSummaries(
                    taskDataAccessObject.getAllTasks(), LocalDate.now(clock));
            presenter.prepareSuccessView(new CompleteTaskOutputData(taskId, task.isDone(), tasks));
        }
    }
}
