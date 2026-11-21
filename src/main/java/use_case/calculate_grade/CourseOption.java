package use_case.calculate_grade;

import java.util.Objects;

/**
 * A course the student can pick in the grade calculator.
 */
public class CourseOption {

    private final String id;
    private final String name;

    /**
     * Creates a course option.
     *
     * @param id   the course's id, not null
     * @param name the course's name as the student sees it, not null
     */
    public CourseOption(String id, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
