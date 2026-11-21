package entity;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Creates new tasks, giving each one a unique id.
 */
public class TaskFactory {

    private final Supplier<String> idGenerator;

    /** Creates a factory that gives each task a random UUID. */
    public TaskFactory() {
        this(() -> UUID.randomUUID().toString());
    }

    /**
     * Creates a factory that takes ids from the given generator. Tests use
     * this to get predictable ids.
     *
     * @param idGenerator supplies a new id each time it is called
     */
    public TaskFactory(Supplier<String> idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * Creates a new task that is not done yet.
     *
     * @param title    the task's title
     * @param courseId the id of the course it belongs to, or null for none
     * @param dueDate  the day it is due, or null for no due date
     * @return the new task
     */
    public Task create(String title, String courseId, LocalDate dueDate) {
        return new Task(idGenerator.get(), title, courseId, dueDate, false);
    }
}
