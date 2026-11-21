package interface_adapter.tasks;

import org.junit.jupiter.api.Test;
import use_case.add_task.AddTaskInputData;
import use_case.complete_task.CompleteTaskInputData;
import use_case.delete_task.DeleteTaskInputData;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests that each task controller passes what the view gives it to its interactor. */
class TaskControllersTest {

    @Test
    void addTaskControllerPassesEveryField() {
        AddTaskInputData[] received = new AddTaskInputData[1];
        LocalDate dueDate = LocalDate.of(2026, 11, 20);

        new AddTaskController(input -> received[0] = input).execute("Lab 6", "c1", dueDate, true);

        assertEquals("Lab 6", received[0].getTitle());
        assertEquals("c1", received[0].getCourseId());
        assertEquals(dueDate, received[0].getDueDate());
        assertTrue(received[0].isPastDueConfirmed());
    }

    @Test
    void completeTaskControllerPassesIdAndDone() {
        CompleteTaskInputData[] received = new CompleteTaskInputData[1];

        new CompleteTaskController(input -> received[0] = input).execute("t1", false);

        assertEquals("t1", received[0].getTaskId());
        assertFalse(received[0].isDone());
    }

    @Test
    void deleteTaskControllerPassesId() {
        DeleteTaskInputData[] received = new DeleteTaskInputData[1];

        new DeleteTaskController(input -> received[0] = input).execute("t1");

        assertEquals("t1", received[0].getTaskId());
    }

    @Test
    void viewTasksControllerRunsTheInteractor() {
        int[] calls = new int[1];

        new ViewTasksController(() -> calls[0]++).execute();

        assertEquals(1, calls[0]);
    }
}
