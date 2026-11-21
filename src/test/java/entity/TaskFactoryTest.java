package entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskFactoryTest {
    @Test
    void createMakesTaskThatIsNotDone() {
        TaskFactory factory = new TaskFactory(() -> "fixed-id");
        LocalDate dueDate = LocalDate.of(2026, 11, 20);

        Task task = factory.create("Finish lab 6", "c1", dueDate);

        assertEquals("fixed-id", task.getId());
        assertEquals("Finish lab 6", task.getTitle());
        assertEquals("c1", task.getCourseId());
        assertEquals(dueDate, task.getDueDate());
        assertFalse(task.isDone());
    }

    @Test
    void defaultFactoryGivesEachTaskItsOwnId() {
        TaskFactory factory = new TaskFactory();

        Task first = factory.create("First", null, null);
        Task second = factory.create("Second", null, null);

        assertFalse(first.getId().isEmpty());
        assertNotEquals(first.getId(), second.getId());
    }
}
