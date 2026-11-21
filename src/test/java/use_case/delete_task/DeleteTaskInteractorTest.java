package use_case.delete_task;

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

/** Tests for {@link DeleteTaskInteractor}. */
public class DeleteTaskInteractorTest {
    /** Every test runs on the fixed date: Monday, November 16, 2026. */
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC);

    private InMemoryTaskDataAccessObject taskDataAccessObject;
    private RecordingPresenter presenter;
    private DeleteTaskInteractor interactor;

    @BeforeEach
    void setUp() {
        taskDataAccessObject = new InMemoryTaskDataAccessObject();
        presenter = new RecordingPresenter();
        interactor = new DeleteTaskInteractor(taskDataAccessObject, presenter, FIXED_CLOCK);
    }

    @Test
    void deletesTaskAndShowsTheRestSorted() {
        taskDataAccessObject.save(new Task("undated", "Someday", null, null, false));
        taskDataAccessObject.save(new Task("gone", "No longer needed", null, TODAY, false));
        taskDataAccessObject.save(new Task("late", "Late", null, TODAY.minusDays(1), false));

        interactor.execute(new DeleteTaskInputData("gone"));

        assertFalse(taskDataAccessObject.existsById("gone"));
        assertEquals(2, taskDataAccessObject.getAllTasks().size());
        assertEquals("gone", presenter.successData.getDeletedTaskId());

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertEquals(2, shown.size());
        assertEquals("late", shown.get(0).getId());
        assertTrue(shown.get(0).isOverdue());
        assertEquals("undated", shown.get(1).getId());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void deletingTheLastTaskShowsAnEmptyList() {
        taskDataAccessObject.save(new Task("t1", "Only task", null, null, false));

        interactor.execute(new DeleteTaskInputData("t1"));

        assertTrue(taskDataAccessObject.getAllTasks().isEmpty());
        assertTrue(presenter.successData.getTasks().isEmpty());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void unknownTaskIsRefusedAndNothingIsDeleted() {
        taskDataAccessObject.save(new Task("t1", "Still here", null, null, false));

        interactor.execute(new DeleteTaskInputData("missing"));

        assertEquals(DeleteTaskInteractor.UNKNOWN_TASK_MESSAGE, presenter.failMessage);
        assertTrue(taskDataAccessObject.existsById("t1"));
        presenter.assertOnlyFailCalled();
    }

    @Test
    void shownListCannotBeChanged() {
        taskDataAccessObject.save(new Task("t1", "Delete me", null, null, false));
        taskDataAccessObject.save(new Task("t2", "Keep me", null, null, false));

        interactor.execute(new DeleteTaskInputData("t1"));

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertThrows(UnsupportedOperationException.class, () -> shown.remove(0));
    }

    /** Records what the interactor sent, so the tests can check it. */
    private static class RecordingPresenter implements DeleteTaskOutputBoundary {
        private DeleteTaskOutputData successData;
        private String failMessage;
        private int calls;

        @Override
        public void prepareSuccessView(DeleteTaskOutputData outputData) {
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
