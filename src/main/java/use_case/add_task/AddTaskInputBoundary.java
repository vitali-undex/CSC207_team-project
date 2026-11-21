package use_case.add_task;

/**
 * The input boundary for the add task use case. The add task controller calls this.
 */
public interface AddTaskInputBoundary {
    
    /**
     * Adds a task, or explains why it cannot be added yet.
     *
     * @param inputData what the student entered
     */
    void execute(AddTaskInputData inputData);
}
