package interface_adapter.tasks;

import use_case.view_tasks.ViewTasksOutputBoundary;
import use_case.view_tasks.ViewTasksOutputData;

import java.util.List;

/**
 * The presenter for the view tasks use case. Fills the course menu and the
 * task table when the tasks tab opens.
 */
public class ViewTasksPresenter implements ViewTasksOutputBoundary {

    private final TasksViewModel tasksViewModel;
    private final TaskRowFormatter formatter = new TaskRowFormatter();

    /**
     * Creates the presenter.
     *
     * @param tasksViewModel the view model this presenter updates
     */
    public ViewTasksPresenter(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
    }

    @Override
    public void prepareSuccessView(ViewTasksOutputData outputData) {
        final TasksState state = tasksViewModel.getState();
        final List<CourseOption> courseOptions = formatter.toCourseOptions(outputData.getCourses());
        state.setCourseOptions(courseOptions);
        state.setRows(formatter.toRows(outputData.getTasks(), courseOptions));
        state.setErrorMessage("");
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged();
    }
}
