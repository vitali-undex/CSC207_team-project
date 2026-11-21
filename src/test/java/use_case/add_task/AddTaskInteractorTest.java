package use_case.add_task;

import data_access.InMemoryTaskDataAccessObject;
import entity.Task;
import entity.TaskFactory;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddTaskInteractorTest {
    /** BEWARE!! Every test runs on the fixed date: Monday, November 16, 2026. */
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC);

    private InMemoryTaskDataAccessObject taskDataAccessObject;
    private RecordingPresenter presenter;
    private AddTaskInteractor interactor;
    private int nextId;

    @BeforeEach
    void setUp() {
        taskDataAccessObject = new InMemoryTaskDataAccessObject();
        presenter = new RecordingPresenter();
        nextId = 1;
        TaskFactory factory = new TaskFactory(() -> "task-" + nextId++);
        interactor = new AddTaskInteractor(taskDataAccessObject, presenter, factory, FIXED_CLOCK);
    }

    @Test
    void addsTaskWithCourseAndDueDate() {
        LocalDate friday = TODAY.plusDays(4);

        interactor.execute(new AddTaskInputData("Study for MIDTERM 2!!!", "csc207", friday, false));

        List<Task> saved = taskDataAccessObject.getAllTasks();
        assertEquals(1, saved.size());
        assertEquals("Study for MIDTERM 2!!!", saved.get(0).getTitle());
        assertEquals("csc207", saved.get(0).getCourseId());
        assertEquals(friday, saved.get(0).getDueDate());
        assertFalse(saved.get(0).isDone());

        assertNotNull(presenter.successData);
        assertEquals("task-1", presenter.successData.getAddedTaskId());
        TaskSummary summary = presenter.successData.getTasks().get(0);
        assertEquals("task-1", summary.getId());
        assertEquals("Study for MIDTERM 2!!!", summary.getTitle());
        assertEquals("csc207", summary.getCourseId());
        assertEquals(friday, summary.getDueDate());
        assertFalse(summary.isDone());
        assertFalse(summary.isOverdue());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void addsTaskWithoutCourseOrDueDate() {
        interactor.execute(new AddTaskInputData("Buy some otter pets", null, null, false));

        Task saved = taskDataAccessObject.getAllTasks().get(0);
        assertNull(saved.getCourseId());
        assertNull(saved.getDueDate());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void trimsTitleAndCourseId() {
        interactor.execute(new AddTaskInputData("  more testing :/  ", " csc207 ", null, false));

        Task saved = taskDataAccessObject.getAllTasks().get(0);
        assertEquals("more testing :/", saved.getTitle());
        assertEquals("csc207", saved.getCourseId());
    }

    @Test
    void emptyTitleIsRefusedAndNothingIsSaved() {
        interactor.execute(new AddTaskInputData("", "csc207", TODAY, false));

        assertEquals(AddTaskInteractor.EMPTY_TITLE_MESSAGE, presenter.failMessage);
        assertTrue(taskDataAccessObject.getAllTasks().isEmpty());
        presenter.assertOnlyFailCalled();
    }

    @Test
    void blankCourseIdMeansNoCourse() {
        interactor.execute(new AddTaskInputData("even more testing", "   ", null, false));

        assertNull(taskDataAccessObject.getAllTasks().get(0).getCourseId());
    }

    @Test
    void titleOfOnlySpacesIsRefused() {
        interactor.execute(new AddTaskInputData("    ", null, null, false));

        assertEquals(AddTaskInteractor.EMPTY_TITLE_MESSAGE, presenter.failMessage);
        assertTrue(taskDataAccessObject.getAllTasks().isEmpty());
        presenter.assertOnlyFailCalled();
    }

    @Test
    void missingTitleIsRefused() {
        interactor.execute(new AddTaskInputData(null, null, null, false));

        assertEquals(AddTaskInteractor.EMPTY_TITLE_MESSAGE, presenter.failMessage);
        assertTrue(taskDataAccessObject.getAllTasks().isEmpty());
    }

    @Test
    void emptyTitleIsRefusedEvenWithPastDueDate() {
        interactor.execute(new AddTaskInputData("", null, TODAY.minusDays(1), false));

        presenter.assertOnlyFailCalled();
    }

    @Test
    void pastDueDateAsksForConfirmationAndSavesNothing() {
        LocalDate yesterday = TODAY.minusDays(1);

        interactor.execute(new AddTaskInputData(" dont be late ", null, yesterday, false));

        assertEquals("dont be late", presenter.confirmationTitle);
        assertEquals(yesterday, presenter.confirmationDueDate);
        assertTrue(taskDataAccessObject.getAllTasks().isEmpty());
        presenter.assertOnlyConfirmationCalled();
    }

    @Test
    void confirmedPastDueDateIsSavedAndShownOverdue() {
        LocalDate yesterday = TODAY.minusDays(1);

        interactor.execute(new AddTaskInputData("so many tests", null, yesterday, true));

        assertEquals(1, taskDataAccessObject.getAllTasks().size());
        assertTrue(presenter.successData.getTasks().get(0).isOverdue());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void taskDueTodayNeedsNoConfirmation() {
        interactor.execute(new AddTaskInputData("how many more?", null, TODAY, false));

        assertEquals(1, taskDataAccessObject.getAllTasks().size());
        assertFalse(presenter.successData.getTasks().get(0).isOverdue());
        presenter.assertOnlySuccessCalled();
    }

    @Test
    void shownListIsSortedByDueDate() {
        taskDataAccessObject.save(new Task("old-1", "me and you", null, null, false));
        taskDataAccessObject.save(new Task("old-2", "will test", null, TODAY.plusDays(7), false));

        interactor.execute(new AddTaskInputData("Tomorrow", null, TODAY.plusDays(1), false));

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertEquals(3, shown.size());
        assertEquals("task-1", shown.get(0).getId());
        assertEquals("old-2", shown.get(1).getId());
        assertEquals("old-1", shown.get(2).getId());
    }

    @Test
    void shownListCannotBeChanged() {
        interactor.execute(new AddTaskInputData("testing", null, null, false));

        List<TaskSummary> shown = presenter.successData.getTasks();
        assertThrows(UnsupportedOperationException.class, () -> shown.remove(0));
    }

    /** Records what the interactor sent, so the tests can check it. */
    private static class RecordingPresenter implements AddTaskOutputBoundary {
        private AddTaskOutputData successData;
        private String failMessage;
        private String confirmationTitle;
        private LocalDate confirmationDueDate;
        private int calls;

        @Override
        public void prepareSuccessView(AddTaskOutputData outputData) {
            successData = outputData;
            calls++;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failMessage = errorMessage;
            calls++;
        }

        @Override
        public void preparePastDueConfirmation(String title, LocalDate dueDate) {
            confirmationTitle = title;
            confirmationDueDate = dueDate;
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

        void assertOnlyConfirmationCalled() {
            assertEquals(1, calls);
            assertNotNull(confirmationDueDate);
        }
    }
}
