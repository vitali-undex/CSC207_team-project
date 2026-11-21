package app;

import data_access.InMemoryTaskDataAccessObject;
import entity.TaskFactory;
import interface_adapter.tasks.AddTaskController;
import interface_adapter.tasks.AddTaskPresenter;
import interface_adapter.tasks.CompleteTaskController;
import interface_adapter.tasks.CompleteTaskPresenter;
import interface_adapter.tasks.DeleteTaskController;
import interface_adapter.tasks.DeleteTaskPresenter;
import interface_adapter.tasks.TasksViewModel;
import interface_adapter.tasks.ViewTasksController;
import interface_adapter.tasks.ViewTasksPresenter;
import use_case.add_task.AddTaskInputBoundary;
import use_case.add_task.AddTaskInteractor;
import use_case.add_task.AddTaskOutputBoundary;
import use_case.complete_task.CompleteTaskInputBoundary;
import use_case.complete_task.CompleteTaskInteractor;
import use_case.complete_task.CompleteTaskOutputBoundary;
import use_case.delete_task.DeleteTaskInputBoundary;
import use_case.delete_task.DeleteTaskInteractor;
import use_case.delete_task.DeleteTaskOutputBoundary;
import use_case.view_tasks.ViewTasksInputBoundary;
import use_case.view_tasks.ViewTasksInteractor;
import use_case.view_tasks.ViewTasksOutputBoundary;
import view.TasksView;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.time.Clock;

/**
 * Creates every object in the app and connects them, as in the ca-login lab
 * code. Each tab of the main window is added with its own add...View method,
 * and each use case with its own add...UseCase method.
 */
public class AppBuilder {

    public static final String WINDOW_TITLE = "Quercus Course Tracker";

    private final JTabbedPane tabs = new JTabbedPane();
    private final Clock clock = Clock.systemDefaultZone();

    // Tasks are kept in memory until they are saved to a JSON file.
    private final InMemoryTaskDataAccessObject taskDataAccessObject =
            new InMemoryTaskDataAccessObject();

    private TasksViewModel tasksViewModel;
    private TasksView tasksView;
    private ViewTasksController viewTasksController;

    /**
     * Adds the "My tasks" tab (use case 5).
     *
     * @return this builder
     */
    public AppBuilder addTasksView() {
        tasksViewModel = new TasksViewModel();
        tasksView = new TasksView(tasksViewModel);
        tabs.addTab(tasksView.getViewName(), tasksView);
        return this;
    }

    /**
     * Adds the add task use case. Call {@link #addTasksView()} first.
     *
     * @return this builder
     */
    public AppBuilder addAddTaskUseCase() {
        final AddTaskOutputBoundary presenter = new AddTaskPresenter(tasksViewModel);
        final AddTaskInputBoundary interactor = new AddTaskInteractor(taskDataAccessObject,
                presenter, new TaskFactory(), clock);
        tasksView.setAddTaskController(new AddTaskController(interactor));
        return this;
    }

    /**
     * Adds the complete task use case. Call {@link #addTasksView()} first.
     *
     * @return this builder
     */
    public AppBuilder addCompleteTaskUseCase() {
        final CompleteTaskOutputBoundary presenter = new CompleteTaskPresenter(tasksViewModel);
        final CompleteTaskInputBoundary interactor =
                new CompleteTaskInteractor(taskDataAccessObject, presenter, clock);
        tasksView.setCompleteTaskController(new CompleteTaskController(interactor));
        return this;
    }

    /**
     * Adds the delete task use case. Call {@link #addTasksView()} first.
     *
     * @return this builder
     */
    public AppBuilder addDeleteTaskUseCase() {
        final DeleteTaskOutputBoundary presenter = new DeleteTaskPresenter(tasksViewModel);
        final DeleteTaskInputBoundary interactor =
                new DeleteTaskInteractor(taskDataAccessObject, presenter, clock);
        tasksView.setDeleteTaskController(new DeleteTaskController(interactor));
        return this;
    }

    /**
     * Adds the view tasks use case, which fills the tab when the app opens.
     * Call {@link #addTasksView()} first.
     *
     * @return this builder
     */
    public AppBuilder addViewTasksUseCase() {
        final ViewTasksOutputBoundary presenter = new ViewTasksPresenter(tasksViewModel);
        final ViewTasksInputBoundary interactor =
                new ViewTasksInteractor(taskDataAccessObject, presenter, clock);
        viewTasksController = new ViewTasksController(interactor);
        return this;
    }

    /**
     * Creates the main window with every tab that was added, and loads the
     * saved tasks.
     *
     * @return the main window, not yet visible
     */
    public JFrame build() {
        final JFrame application = new JFrame(WINDOW_TITLE);
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.add(tabs);

        if (viewTasksController != null) {
            viewTasksController.execute();
        }
        return application;
    }
}
