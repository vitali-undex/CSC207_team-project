package use_case.view_tasks;

import use_case.tasks.TaskListMapper;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * The view tasks use case: loads the saved tasks, sorted by due date, and the
 * courses a new task can be linked to. Runs when the tasks tab opens.
 */
public class ViewTasksInteractor implements ViewTasksInputBoundary {

    private final ViewTasksDataAccessInterface taskDataAccessObject;
    private final ViewTasksOutputBoundary presenter;
    private final Clock clock;
    private final TaskListMapper taskListMapper = new TaskListMapper();

    /**
     * Creates the interactor.
     *
     * @param taskDataAccessObject where tasks and courses are saved
     * @param presenter            receives the result
     * @param clock                gives today's date; tests pass a fixed clock
     */
    public ViewTasksInteractor(ViewTasksDataAccessInterface taskDataAccessObject,
                               ViewTasksOutputBoundary presenter,
                               Clock clock) {
        this.taskDataAccessObject = taskDataAccessObject;
        this.presenter = presenter;
        this.clock = clock;
    }

    @Override
    public void execute() {
        final List<TaskSummary> tasks = taskListMapper.toSortedSummaries(
                taskDataAccessObject.getAllTasks(), LocalDate.now(clock));
        final List<CourseChoice> courses = taskDataAccessObject.getSavedCourses();
        presenter.prepareSuccessView(new ViewTasksOutputData(tasks, courses));
    }
}
