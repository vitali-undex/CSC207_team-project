package use_case.view_tasks;

import data_access.InMemoryTaskDataAccessObject;
import entity.Task;
import org.junit.jupiter.api.Test;
import use_case.tasks.TaskSummary;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link ViewTasksInteractor}. */
public class ViewTasksInteractorTest {
    /** Every test runs on the fixed date: Monday, November 16, 2026. */
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC);

    private final RecordingPresenter presenter = new RecordingPresenter();

    @Test
    void noSavedTasksOrCoursesGivesEmptyLists() {
        InMemoryTaskDataAccessObject taskDataAccessObject = new InMemoryTaskDataAccessObject();

        new ViewTasksInteractor(taskDataAccessObject, presenter, FIXED_CLOCK).execute();

        assertTrue(presenter.outputData.getTasks().isEmpty());
        assertTrue(presenter.outputData.getCourses().isEmpty());
        assertEquals(1, presenter.calls);
    }

    @Test
    void savedTasksAreShownSortedWithOverdueMarked() {
        InMemoryTaskDataAccessObject taskDataAccessObject = new InMemoryTaskDataAccessObject();
        taskDataAccessObject.save(new Task("undated", "Someday", null, null, false));
        taskDataAccessObject.save(new Task("soon", "Soon", "c1", TODAY.plusDays(2), false));
        taskDataAccessObject.save(new Task("late", "Late", null, TODAY.minusDays(1), false));
        taskDataAccessObject.save(new Task("done", "Done late", null, TODAY.minusDays(3), true));

        new ViewTasksInteractor(taskDataAccessObject, presenter, FIXED_CLOCK).execute();

        List<TaskSummary> shown = presenter.outputData.getTasks();
        assertEquals(4, shown.size());
        assertEquals("done", shown.get(0).getId());
        assertFalse(shown.get(0).isOverdue());
        assertEquals("late", shown.get(1).getId());
        assertTrue(shown.get(1).isOverdue());
        assertEquals("soon", shown.get(2).getId());
        assertEquals("c1", shown.get(2).getCourseId());
        assertEquals("undated", shown.get(3).getId());
    }

    @Test
    void savedCoursesArePassedThrough() {
        InMemoryTaskDataAccessObject taskDataAccessObject = new InMemoryTaskDataAccessObject(
                Arrays.asList(new CourseChoice("c1", "CSC207H1"),
                        new CourseChoice("c2", "MAT237Y1")));

        new ViewTasksInteractor(taskDataAccessObject, presenter, FIXED_CLOCK).execute();

        List<CourseChoice> courses = presenter.outputData.getCourses();
        assertEquals(2, courses.size());
        assertEquals("c1", courses.get(0).getId());
        assertEquals("CSC207H1", courses.get(0).getCode());
        assertEquals("c2", courses.get(1).getId());
        assertEquals("MAT237Y1", courses.get(1).getCode());
    }

    /** Records what the interactor sent, so the tests can check it. */
    private static class RecordingPresenter implements ViewTasksOutputBoundary {
        private ViewTasksOutputData outputData;
        private int calls;

        @Override
        public void prepareSuccessView(ViewTasksOutputData data) {
            assertNotNull(data);
            outputData = data;
            calls++;
        }
    }
}
