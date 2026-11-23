package use_case.view_tasks;

/**
 * A course the student can link a task to. Until the course list from
 * Quercus is saved, there are none.
 */
public class CourseChoice {

    private final String id;
    private final String code;

    /**
     * Creates a course choice.
     *
     * @param id   the course's id
     * @param code the course's code, such as "CSC207H1"
     */
    public CourseChoice(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
