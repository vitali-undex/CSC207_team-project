package interface_adapter.tasks;

import use_case.add_task.AddTaskOutputBoundary;
import use_case.add_task.AddTaskOutputData;

import java.time.LocalDate;

/**
 * The presenter for the add task use case.
 */
public class AddTaskPresenter implements AddTaskOutputBoundary {

    private final TasksViewModel tasksViewModel;
    private final TaskRowFormatter formatter = new TaskRowFormatter();

    /**
     * Creates the presenter.
     *
     * @param tasksViewModel the view model this presenter updates
     */
    public AddTaskPresenter(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
    }

    @Override
    public void prepareSuccessView(AddTaskOutputData outputData) {
        final TasksState state = tasksViewModel.getState();
        state.setRows(formatter.toRows(outputData.getTasks(), state.getCourseOptions()));
        state.setErrorMessage("");
        state.setPastDueQuestion("");
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged(TasksViewModel.TASK_ADDED);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final TasksState state = tasksViewModel.getState();
        state.setErrorMessage(errorMessage);
        state.setPastDueQuestion("");
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged();
    }

    @Override
    public void preparePastDueConfirmation(String title, LocalDate dueDate) {
        final TasksState state = tasksViewModel.getState();
        state.setErrorMessage("");
        state.setPastDueQuestion("\"" + title + "\" was due on " + formatter.formatDate(dueDate)
                + ", which has already passed. Add it anyway?");
        tasksViewModel.setState(state);
        tasksViewModel.firePropertyChanged(TasksViewModel.PAST_DUE_QUESTION);
    }
}
