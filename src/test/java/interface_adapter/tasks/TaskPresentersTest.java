package interface_adapter.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.add_task.AddTaskOutputData;
import use_case.complete_task.CompleteTaskOutputData;
import use_case.delete_task.DeleteTaskOutputData;
import use_case.tasks.TaskSummary;
import use_case.view_tasks.CourseChoice;
import use_case.view_tasks.ViewTasksOutputData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the four task presenters, which all update the one tasks view model. */
class TaskPresentersTest {

    private static final List<TaskSummary> ONE_TASK = Collections.singletonList(
            new TaskSummary("t1", "Lab 6", "c1", LocalDate.of(2026, 11, 20), false, false));

    private TasksViewModel viewModel;
    private List<String> firedEvents;

    @BeforeEach
    void setUp() {
        viewModel = new TasksViewModel();
        firedEvents = new ArrayList<>();
        viewModel.addPropertyChangeListener(event -> firedEvents.add(event.getPropertyName()));
    }

    @Test
    void newViewModelIsEmptyWithOnlyNoneInTheCourseMenu() {
        TasksState state = viewModel.getState();

        assertEquals(TasksViewModel.VIEW_NAME, viewModel.getViewName());
        assertTrue(state.getRows().isEmpty());
        assertEquals(Collections.singletonList(CourseOption.NONE), state.getCourseOptions());
        assertEquals("", state.getErrorMessage());
        assertEquals("", state.getPastDueQuestion());
    }

    @Test
    void viewTasksFillsCourseMenuAndRows() {
        viewModel.getState().setErrorMessage("old error");

        new ViewTasksPresenter(viewModel).prepareSuccessView(new ViewTasksOutputData(ONE_TASK,
                Collections.singletonList(new CourseChoice("c1", "CSC207H1"))));

        TasksState state = viewModel.getState();
        assertEquals(2, state.getCourseOptions().size());
        assertSame(CourseOption.NONE, state.getCourseOptions().get(0));
        assertEquals("CSC207H1", state.getCourseOptions().get(1).getLabel());
        assertEquals(1, state.getRows().size());
        assertEquals("CSC207H1", state.getRows().get(0).getCourse());
        assertEquals("", state.getErrorMessage());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }

    @Test
    void addTaskSuccessShowsRowsAndClearsMessages() {
        viewModel.getState().setErrorMessage("old error");
        viewModel.getState().setPastDueQuestion("old question");

        new AddTaskPresenter(viewModel).prepareSuccessView(new AddTaskOutputData("t1", ONE_TASK));

        TasksState state = viewModel.getState();
        assertEquals("t1", state.getRows().get(0).getId());
        assertEquals("Fri, Nov 20, 2026", state.getRows().get(0).getDue());
        assertEquals("", state.getErrorMessage());
        assertEquals("", state.getPastDueQuestion());
        assertEquals(Collections.singletonList(TasksViewModel.TASK_ADDED), firedEvents);
    }

    @Test
    void addTaskFailShowsTheErrorAndKeepsTheRows() {
        new AddTaskPresenter(viewModel).prepareSuccessView(new AddTaskOutputData("t1", ONE_TASK));
        firedEvents.clear();

        new AddTaskPresenter(viewModel).prepareFailView("Please enter a title for the task.");

        TasksState state = viewModel.getState();
        assertEquals("Please enter a title for the task.", state.getErrorMessage());
        assertEquals(1, state.getRows().size());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }

    @Test
    void pastDueConfirmationAsksTheStudent() {
        viewModel.getState().setErrorMessage("old error");

        new AddTaskPresenter(viewModel).preparePastDueConfirmation("Lab 5",
                LocalDate.of(2026, 11, 15));

        TasksState state = viewModel.getState();
        assertEquals("\"Lab 5\" was due on Sun, Nov 15, 2026, which has already passed. "
                + "Add it anyway?", state.getPastDueQuestion());
        assertEquals("", state.getErrorMessage());
        assertEquals(Collections.singletonList(TasksViewModel.PAST_DUE_QUESTION), firedEvents);
    }

    @Test
    void completeTaskSuccessShowsRowsAndClearsTheError() {
        viewModel.getState().setErrorMessage("old error");
        List<TaskSummary> doneTask = Collections.singletonList(
                new TaskSummary("t1", "Lab 6", null, null, true, false));

        new CompleteTaskPresenter(viewModel).prepareSuccessView(
                new CompleteTaskOutputData("t1", true, doneTask));

        TasksState state = viewModel.getState();
        assertTrue(state.getRows().get(0).isDone());
        assertEquals("", state.getErrorMessage());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }

    @Test
    void completeTaskFailShowsTheError() {
        new CompleteTaskPresenter(viewModel).prepareFailView("That task no longer exists.");

        assertEquals("That task no longer exists.", viewModel.getState().getErrorMessage());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }

    @Test
    void deleteTaskSuccessShowsTheRemainingRows() {
        new AddTaskPresenter(viewModel).prepareSuccessView(new AddTaskOutputData("t1", ONE_TASK));
        viewModel.getState().setErrorMessage("old error");
        firedEvents.clear();

        new DeleteTaskPresenter(viewModel).prepareSuccessView(
                new DeleteTaskOutputData("t1", Collections.emptyList()));

        TasksState state = viewModel.getState();
        assertTrue(state.getRows().isEmpty());
        assertEquals("", state.getErrorMessage());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }

    @Test
    void deleteTaskFailShowsTheError() {
        new DeleteTaskPresenter(viewModel).prepareFailView("That task no longer exists.");

        assertEquals("That task no longer exists.", viewModel.getState().getErrorMessage());
        assertEquals(Collections.singletonList("state"), firedEvents);
    }
}
