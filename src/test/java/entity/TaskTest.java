package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);
    @Test
    void constructorKeepsAllFields() {
        Task task = new Task("t1", "Study for the midterm", "c1", TODAY, true);

        assertEquals("t1", task.getId());
        assertEquals("Study for the midterm", task.getTitle());
        assertEquals("c1", task.getCourseId());
        assertEquals(TODAY, task.getDueDate());
        assertTrue(task.isDone());
        assertTrue(task.hasCourse());
        assertTrue(task.hasDueDate());
    }

    @Test
    void courseAndDueDateAreOptional() {
        Task task = new Task("t1", "Buy a calculator", null, null, false);

        assertNull(task.getCourseId());
        assertNull(task.getDueDate());
        assertFalse(task.hasCourse());
        assertFalse(task.hasDueDate());
        assertFalse(task.isDone());
    }

    @Test
    void idAndTitleAreRequired() {
        assertThrows(NullPointerException.class, () -> new Task(null, "Title", null, null, false));
        assertThrows(NullPointerException.class, () -> new Task("t1", null, null, null, false));
    }

    @Test
    void completeAndReopenChangeDone() {
        Task task = new Task("t1", "Read chapter 3", null, null, false);

        task.complete();
        assertTrue(task.isDone());

        task.reopen();
        assertFalse(task.isDone());
    }

    @Test
    void taskDueBeforeTodayIsOverdue() {
        Task task = new Task("t1", "Late", null, TODAY.minusDays(1), false);

        assertTrue(task.isOverdue(TODAY));
    }

    @Test
    void taskDueTodayOrLaterIsNotOverdue() {
        Task dueToday = new Task("t1", "Today", null, TODAY, false);
        Task dueTomorrow = new Task("t2", "Tomorrow", null, TODAY.plusDays(1), false);

        assertFalse(dueToday.isOverdue(TODAY));
        assertFalse(dueTomorrow.isOverdue(TODAY));
    }

    @Test
    void taskWithoutDueDateIsNeverOverdue() {
        Task task = new Task("t1", "Someday", null, null, false);

        assertFalse(task.isOverdue(TODAY));
    }

    @Test
    void doneTaskIsNotOverdue() {
        Task task = new Task("t1", "Finished late", null, TODAY.minusDays(3), true);

        assertFalse(task.isOverdue(TODAY));
    }

    @Test
    void dueDateOrderPutsEarliestFirstAndUndatedLast() {
        Task undated = new Task("t1", "Undated", null, null, false);
        Task later = new Task("t2", "Later", null, TODAY.plusDays(5), false);
        Task sooner = new Task("t3", "Sooner", null, TODAY.plusDays(1), false);

        List<Task> tasks = new ArrayList<>(Arrays.asList(undated, later, sooner));
        tasks.sort(Task.DUE_DATE_ORDER);

        assertEquals(Arrays.asList(sooner, later, undated), tasks);
    }

    @Test
    void dueDateOrderBreaksTiesByTitleIgnoringCase() {
        Task zebra = new Task("t1", "zebra", null, TODAY, false);
        Task apple = new Task("t2", "Apple", null, TODAY, false);
        Task mango = new Task("t3", "mango", null, null, false);
        Task banana = new Task("t4", "Banana", null, null, false);

        List<Task> tasks = new ArrayList<>(Arrays.asList(zebra, mango, apple, banana));
        tasks.sort(Task.DUE_DATE_ORDER);

        assertEquals(Arrays.asList(apple, zebra, banana, mango), tasks);
    }
}
