package interface_adapter.tasks;

import org.junit.jupiter.api.Test;
import use_case.tasks.TaskSummary;
import use_case.view_tasks.CourseChoice;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskRowFormatterTest {

    private final TaskRowFormatter formatter = new TaskRowFormatter();

    @Test
    void rowCopiesEveryFieldAndFormatsTheDueDate() {
        TaskSummary task = new TaskSummary("t1", "Lab 6", null, LocalDate.of(2026, 11, 20),
                true, false);

        TaskRow row = formatter.toRows(Collections.singletonList(task),
                Collections.singletonList(CourseOption.NONE)).get(0);

        assertEquals("t1", row.getId());
        assertTrue(row.isDone());
        assertEquals("Lab 6", row.getTitle());
        assertEquals("", row.getCourse());
        assertEquals("Fri, Nov 20, 2026", row.getDue());
        assertFalse(row.isOverdue());
    }

    @Test
    void rowShowsCourseCodeAndOverdue() {
        List<CourseOption> options = Arrays.asList(CourseOption.NONE,
                new CourseOption("c1", "CSC207H1"));
        TaskSummary task = new TaskSummary("t1", "Late", "c1", LocalDate.of(2026, 11, 15),
                false, true);

        TaskRow row = formatter.toRows(Collections.singletonList(task), options).get(0);

        assertEquals("CSC207H1", row.getCourse());
        assertEquals("Sun, Nov 15, 2026", row.getDue());
        assertTrue(row.isOverdue());
    }

    @Test
    void unknownCourseShowsItsIdAndNoDueDateShowsNothing() {
        TaskSummary task = new TaskSummary("t1", "Old course", "c9", null, false, false);

        TaskRow row = formatter.toRows(Collections.singletonList(task),
                Collections.singletonList(CourseOption.NONE)).get(0);

        assertEquals("c9", row.getCourse());
        assertEquals("", row.getDue());
    }

    @Test
    void rowsKeepTheGivenOrder() {
        List<TaskSummary> tasks = Arrays.asList(
                new TaskSummary("b", "Second", null, null, false, false),
                new TaskSummary("a", "First", null, null, false, false));

        List<TaskRow> rows = formatter.toRows(tasks, Collections.emptyList());

        assertEquals("b", rows.get(0).getId());
        assertEquals("a", rows.get(1).getId());
    }

    @Test
    void courseOptionsStartWithNone() {
        List<CourseOption> options = formatter.toCourseOptions(
                Arrays.asList(new CourseChoice("c1", "CSC207H1"),
                        new CourseChoice("c2", "MAT237Y1")));

        assertEquals(3, options.size());
        assertSame(CourseOption.NONE, options.get(0));
        assertNull(options.get(0).getCourseId());
        assertEquals("None", options.get(0).toString());
        assertEquals("c1", options.get(1).getCourseId());
        assertEquals("CSC207H1", options.get(1).getLabel());
        assertEquals("MAT237Y1", options.get(2).toString());
    }

    @Test
    void noCoursesGivesOnlyNone() {
        List<CourseOption> options = formatter.toCourseOptions(Collections.emptyList());

        assertEquals(Collections.singletonList(CourseOption.NONE), options);
    }

    @Test
    void formatDateOfNullIsEmpty() {
        assertEquals("", formatter.formatDate(null));
    }
}
