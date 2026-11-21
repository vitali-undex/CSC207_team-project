package use_case.add_task;

import entity.Task;
import entity.TaskFactory;
import use_case.tasks.TaskListMapper;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * The add task use case (use case 5, steps 1 to 4).
 *
 * <ul>
 *   <li>An empty title is refused and nothing is saved (extension 2a).</li>
 *   <li>A due date before today needs the student's confirmation first
 *       (extension 1a). A task due today needs no confirmation.</li>
 *   <li>Otherwise the task is saved and the sorted task list is shown.</li>
 * </ul>
 */
public class AddTaskInteractor implements AddTaskInputBoundary {

    /** Shown when the student tries to add a task without a title. */
    public static final String EMPTY_TITLE_MESSAGE = "Please enter a title for the task.";

    private final AddTaskDataAccessInterface taskDataAccessObject;
    private final AddTaskOutputBoundary presenter;
    private final TaskFactory taskFactory;
    private final Clock clock;
    private final TaskListMapper taskListMapper = new TaskListMapper();

    /**
     * Creates the interactor.
     *
     * @param taskDataAccessObject where tasks are saved
     * @param presenter            receives the result
     * @param taskFactory          creates the new task
     * @param clock                gives today's date; tests pass a fixed clock
     */
    public AddTaskInteractor(AddTaskDataAccessInterface taskDataAccessObject,
                             AddTaskOutputBoundary presenter,
                             TaskFactory taskFactory,
                             Clock clock) {
        this.taskDataAccessObject = taskDataAccessObject;
        this.presenter = presenter;
        this.taskFactory = taskFactory;
        this.clock = clock;
    }

    @Override
    public void execute(AddTaskInputData inputData) {
        final String title = trimToEmpty(inputData.getTitle());
        final LocalDate dueDate = inputData.getDueDate();
        final LocalDate today = LocalDate.now(clock);

        if (title.isEmpty()) {
            presenter.prepareFailView(EMPTY_TITLE_MESSAGE);
        }
        else if (dueDate != null && dueDate.isBefore(today) && !inputData.isPastDueConfirmed()) {
            presenter.preparePastDueConfirmation(title, dueDate);
        }
        else {
            final String courseId = emptyToNull(trimToEmpty(inputData.getCourseId()));
            final Task task = taskFactory.create(title, courseId, dueDate);
            taskDataAccessObject.save(task);

            final List<TaskSummary> tasks =
                    taskListMapper.toSortedSummaries(taskDataAccessObject.getAllTasks(), today);
            presenter.prepareSuccessView(new AddTaskOutputData(task.getId(), tasks));
        }
    }

    private static String trimToEmpty(String text) {
        if (text == null) {
            return "";
        }
        return text.trim();
    }

    private static String emptyToNull(String text) {
        if (text.isEmpty()) {
            return null;
        }
        return text;
    }
}