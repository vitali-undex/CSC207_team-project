package use_case.delete_task;

import use_case.tasks.TaskListMapper;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * The delete task use case (use case 5, extension 5b). The task is removed
 * and the tasks that are left are shown. If the task no longer exists,
 * nothing is changed.
 */
public class DeleteTaskInteractor implements DeleteTaskInputBoundary {

    /** Shown when the task to delete is not saved. */
    public static final String UNKNOWN_TASK_MESSAGE = "That task no longer exists.";

    private final DeleteTaskDataAccessInterface taskDataAccessObject;
    private final DeleteTaskOutputBoundary presenter;
    private final Clock clock;
    private final TaskListMapper taskListMapper = new TaskListMapper();

    /**
     * Creates the interactor.
     *
     * @param taskDataAccessObject where tasks are saved
     * @param presenter            receives the result
     * @param clock                gives today's date; tests pass a fixed clock
     */
    public DeleteTaskInteractor(DeleteTaskDataAccessInterface taskDataAccessObject,
                                DeleteTaskOutputBoundary presenter,
                                Clock clock) {
        this.taskDataAccessObject = taskDataAccessObject;
        this.presenter = presenter;
        this.clock = clock;
    }

    @Override
    public void execute(DeleteTaskInputData inputData) {
        final String taskId = inputData.getTaskId();

        if (!taskDataAccessObject.existsById(taskId)) {
            presenter.prepareFailView(UNKNOWN_TASK_MESSAGE);
        }
        else {
            taskDataAccessObject.delete(taskId);

            final List<TaskSummary> tasks = taskListMapper.toSortedSummaries(
                    taskDataAccessObject.getAllTasks(), LocalDate.now(clock));
            presenter.prepareSuccessView(new DeleteTaskOutputData(taskId, tasks));
        }
    }
}
