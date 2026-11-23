package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

/**
 * A group of assignments that the syllabus weights together, such as
 * "Quizzes" or "Midterm".
 */
public class AssignmentGroup {

    private final String id;
    private final String name;
    private final List<GradedAssignment> assignments;

    /**
     * Creates an assignment group.
     *
     * @param id          a unique id for the group within its course, not null
     * @param name        the group's name, not null
     * @param assignments the assignments in the group, not null
     */
    public AssignmentGroup(String id, String name, List<GradedAssignment> assignments) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.assignments = List.copyOf(assignments);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the group's assignments.
     *
     * @return the assignments, which can't be changed
     */
    public List<GradedAssignment> getAssignments() {
        return assignments;
    }

    /**
     * Returns how many assignments in the group have been graded.
     *
     * @return the number of graded assignments
     */
    public int getGradedCount() {
        int count = 0;
        for (GradedAssignment assignment : assignments) {
            if (assignment.isGraded()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the group's average over its graded work. Ungraded assignments
     * are left out; the strategy, with any rules wrapped around it, decides
     * how the rest count.
     *
     * @param rule how the graded marks are averaged, not null
     * @return the average as a percentage, or empty if nothing is graded yet
     */
    public OptionalDouble average(AveragingStrategy rule) {
        Objects.requireNonNull(rule, "rule");
        final List<GradedAssignment> graded = new ArrayList<>();
        for (GradedAssignment assignment : assignments) {
            if (assignment.isGraded()) {
                graded.add(assignment);
            }
        }
        if (graded.isEmpty()) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(rule.average(graded));
    }
}
