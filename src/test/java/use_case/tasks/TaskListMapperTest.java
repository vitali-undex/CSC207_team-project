package use_case.tasks;

import entity.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListMapperTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);

    private final TaskListMapper mapper = new TaskListMapper();

    @Test
    void summariesAreSortedByDueDate() {
        Task undated = new Task("t1", "Undated", null, null, false);
        Task nextWeek = new Task("t2", "Next week", null, TODAY.plusDays(7), false);
        Task tomorrow = new Task("t3", "Tomorrow", null, TODAY.plusDays(1), false);

        List<TaskSummary> summaries =
                mapper.toSortedSummaries(Arrays.asList(undated, nextWeek, tomorrow), TODAY);

        assertEquals(3, summaries.size());
        assertEquals("t3", summaries.get(0).getId());
        assertEquals("t2", summaries.get(1).getId());
        assertEquals("t1", summaries.get(2).getId());
    }

    @Test
    void summaryCopiesEveryField() {
        Task overdue = new Task("t1", "Overdue", "c1", TODAY.minusDays(2), false);
        Task done = new Task("t2", "Done", null, null, true);

        List<TaskSummary> summaries = mapper.toSortedSummaries(Arrays.asList(overdue, done), TODAY);

        TaskSummary first = summaries.get(0);
        assertEquals("t1", first.getId());
        assertEquals("Overdue", first.getTitle());
        assertEquals("c1", first.getCourseId());
        assertEquals(TODAY.minusDays(2), first.getDueDate());
        assertFalse(first.isDone());
        assertTrue(first.isOverdue());

        TaskSummary second = summaries.get(1);
        assertNull(second.getCourseId());
        assertNull(second.getDueDate());
        assertTrue(second.isDone());
        assertFalse(second.isOverdue());
    }

    @Test
    void givenListIsNotChanged() {
        Task later = new Task("t1", "Later", null, TODAY.plusDays(3), false);
        Task sooner = new Task("t2", "Sooner", null, TODAY.plusDays(1), false);
        List<Task> tasks = new ArrayList<>(Arrays.asList(later, sooner));

        mapper.toSortedSummaries(tasks, TODAY);

        assertEquals(Arrays.asList(later, sooner), tasks);
    }

    @Test
    void noTasksGivesEmptyList() {
        assertTrue(mapper.toSortedSummaries(Collections.emptyList(), TODAY).isEmpty());
    }
}
