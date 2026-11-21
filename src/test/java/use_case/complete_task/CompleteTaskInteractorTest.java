package use_case.complete_task;

import data_access.InMemoryTaskDataAccessObject;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link CompleteTaskInteractor}. */
public class CompleteTaskInteractorTest {
    /** Every test runs on the fixed date: Monday, November 16, 2026. */
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC);

    private InMemoryTaskDataAccessObject taskDataAccessObject;
    private RecordingPresenter presenter;
    private CompleteTaskInteractor interactor;

    @BeforeEach
    void setUp() {
        taskDataAccessObject = new InMemoryTaskDataAccessObject();
        presenter = new RecordingPresenter();
        interactor = new CompleteTaskInteractor(taskDataAccessObject, presenter, FIXED_CLOCK);
    }

    @Test
    void tickingMarksTaskAsDoneAndSavesIt() {
        taskDataAccessObject.save(new Task("t1", "Read chapter 3", null, null, false));

        interactor.execute(new CompleteTaskInputData("t1", true));

        assertTrue(taskDataAccessObject.getTask("t1").isDone());
        assertEquals("t1", presenter.successData.getTaskId());
        assertTrue(presenter.successData.isDone());
        assertTrue(presenter.successData.getTasks().get(0).isDone());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void untickingMarksTaskAsNotDoneAndSavesIt() {
        taskDataAccessObject.save(new Task("t1", "Ticked by mistake", null, null, true));

        interactor.execute(new CompleteTaskInputData("t1", false));

        assertFalse(taskDataAccessObject.getTask("t1").isDone());
        assertFalse(presenter.successData.isDone());
        assertFalse(presenter.successData.getTasks().get(0).isDone());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void tickingAnOverdueTaskMeansItIsNoLongerOverdue() {
        taskDataAccessObject.save(new Task("t1", "Late", null, TODAY.minusDays(2), false));

        interactor.execute(new CompleteTaskInputData("t1", true));

        assertFalse(presenter.successData.getTasks().get(0).isOverdue());
    }

    @Test
    void untickingAnOldTaskShowsItAsOverdueAgain() {
        taskDataAccessObject.save(new Task("t1", "Late", null, TODAY.minusDays(2), true));

        interactor.execute(new CompleteTaskInputData("t1", false));

        assertTrue(presenter.successData.getTasks().get(0).isOverdue());
    }

    @Test
    void shownListHasEveryTaskSortedByDueDate() {
        taskDataAccessObject.save(new Task("undated", "Someday", null, null, false));
        taskDataAccessObject.save(new Task("later", "Later", null, TODAY.plusDays(5), false));
        taskDataAccessObject.save(new Task("sooner", "Sooner", null, TODAY.plusDays(1), false));

        interactor.execute(new CompleteTaskInputData("later", true));

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertEquals(3, shown.size());
        assertEquals("sooner", shown.get(0).getId());
        assertEquals("later", shown.get(1).getId());
        assertEquals("undated", shown.get(2).getId());
        assertFalse(shown.get(0).isDone());
        assertTrue(shown.get(1).isDone());
    }

    @Test
    void unknownTaskIsRefusedAndNothingIsSaved() {
        taskDataAccessObject.save(new Task("t1", "Still here", null, null, false));

        interactor.execute(new CompleteTaskInputData("missing", true));

        assertEquals(CompleteTaskInteractor.UNKNOWN_TASK_MESSAGE, presenter.failMessage);
        assertEquals(1, taskDataAccessObject.getAllTasks().size());
        assertFalse(taskDataAccessObject.getTask("t1").isDone());
        presenter.assertOnlyFailCalled();
    }

    @Test
    void shownListCannotBeChanged() {
        taskDataAccessObject.save(new Task("t1", "Task", null, null, false));

        interactor.execute(new CompleteTaskInputData("t1", true));

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertThrows(UnsupportedOperationException.class, () -> shown.remove(0));
    }

    /** Records what the interactor sent, so the tests can check it. */
    private static class RecordingPresenter implements CompleteTaskOutputBoundary {
        private CompleteTaskOutputData successData;
        private String failMessage;
        private int calls;

        @Override
        public void prepareSuccessView(CompleteTaskOutputData outputData) {
            successData = outputData;
            calls++;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failMessage = errorMessage;
            calls++;
        }

        void assertOnlySuccessCalled() {
            assertEquals(1, calls);
            assertNotNull(successData);
        }

        void assertOnlyFailCalled() {
            assertEquals(1, calls);
            assertNotNull(failMessage);
        }
    }
}
