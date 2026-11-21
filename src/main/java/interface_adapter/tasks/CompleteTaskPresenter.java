package interface_adapter.tasks;

import use_case.complete_task.CompleteTaskOutputBoundary;
import use_case.complete_task.CompleteTaskOutputData;

/**
 * The presenter for the complete task use case.
 */
public class CompleteTaskPresenter implements CompleteTaskOutputBoundary {

    private final TasksViewModel tasksViewModel;
    private final TaskRowFormatter formatter = new TaskRowFormatter();

    /**
     * Creates the presenter.
     *
     * @param tasksViewModel the view model this presenter updates
     */
    public CompleteTaskPresenter(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
    }

    @Override
    public void prepareSuccessView(CompleteTaskOutputData outputData) {
        final TasksState state = tasksViewModel.getState();
        state.setRows(formatter.toRows(outputData.getTasks(), state.getCourseOptions()));
        state.setErrorMessage("");
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final TasksState state = tasksViewModel.getState();
        state.setErrorMessage(errorMessage);
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged();
    }
}
