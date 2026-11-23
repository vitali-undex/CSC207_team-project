package interface_adapter.tasks;

import use_case.delete_task.DeleteTaskOutputBoundary;
import use_case.delete_task.DeleteTaskOutputData;

/**
 * The presenter for the delete task use case.
 */
public class DeleteTaskPresenter implements DeleteTaskOutputBoundary {

    private final TasksViewModel tasksViewModel;
    private final TaskRowFormatter formatter = new TaskRowFormatter();

    /**
     * Creates the presenter.
     *
     * @param tasksViewModel the view model this presenter updates
     */
    public DeleteTaskPresenter(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
    }

    @Override
    public void prepareSuccessView(DeleteTaskOutputData outputData) {
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
