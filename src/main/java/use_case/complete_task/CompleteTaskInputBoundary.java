package use_case.complete_task;

/**
 * The input boundary for the complete task use case. The complete task
 * controller calls this.
 */
public interface CompleteTaskInputBoundary {

    /**
     * Ticks a task off, or unticks it.
     *
     * @param inputData which task, and whether it is now done
     */
    void execute(CompleteTaskInputData inputData);
}
