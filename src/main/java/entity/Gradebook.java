package entity;

import java.util.List;
import java.util.Objects;

/**
 * One course's assignment groups and the student's scores in them.
 */
public class Gradebook {

    private final String courseId;
    private final String courseName;
    private final List<AssignmentGroup> groups;

    /**
     * Creates a gradebook.
     *
     * @param courseId   the course's id, not null
     * @param courseName the course's name as the student sees it, not null
     * @param groups     the course's assignment groups, not null
     */
    public Gradebook(String courseId, String courseName, List<AssignmentGroup> groups) {
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.courseName = Objects.requireNonNull(courseName, "courseName");
        this.groups = List.copyOf(groups);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    /**
     * Returns the course's assignment groups.
     *
     * @return the groups, which can't be changed
     */
    public List<AssignmentGroup> getGroups() {
        return groups;
    }
}
