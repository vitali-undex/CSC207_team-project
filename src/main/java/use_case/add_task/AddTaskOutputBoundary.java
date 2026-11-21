package use_case.add_task;

import java.time.LocalDate;

/**
 * The output boundary for the add task use case. The add task presenter
 * implements this.
 */
public interface AddTaskOutputBoundary {

    /**
     * Shows the updated task list after a task was added.
     *
     * @param outputData the new task's id and the sorted task list
     */
    void prepareSuccessView(AddTaskOutputData outputData);

    /**
     * Shows why the task was not added. Nothing was saved.
     *
     * @param errorMessage the message to show the student
     */
    void prepareFailView(String errorMessage);

    /**
     * Asks the student to confirm a due date that has already passed
     * (extension 1a). Nothing was saved. If the student says yes, the
     * controller sends the same task again with the confirmation set.
     *
     * @param title   the task's title, without spaces at either end
     * @param dueDate the due date that has passed
     */
    void preparePastDueConfirmation(String title, LocalDate dueDate);
}
