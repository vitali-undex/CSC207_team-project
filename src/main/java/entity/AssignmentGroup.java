package entity;

import java.util.ArrayList;
import java.util.Comparator;
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
     * Returns the group's average after dropping its lowest marks, counting
     * only graded work. The lowest percentages are dropped first (of two equal
     * percentages, the one worth more points goes first, which helps the
     * student more), and at least one graded mark is always kept.
     *
     * @param dropLowest how many of the lowest marks to drop, 0 to keep them all
     * @param averaging  how the marks that are left are averaged, not null
     * @return the average as a percentage, or empty if nothing is graded yet
     * @throws IllegalArgumentException if dropLowest is negative
     */
    public OptionalDouble average(int dropLowest, AveragingStrategy averaging) {
        Objects.requireNonNull(averaging, "averaging");
        if (dropLowest < 0) {
            throw new IllegalArgumentException("dropLowest must not be negative");
        }
        final List<GradedAssignment> graded = new ArrayList<>();
        for (GradedAssignment assignment : assignments) {
            if (assignment.isGraded()) {
                graded.add(assignment);
            }
        }
        if (graded.isEmpty()) {
            return OptionalDouble.empty();
        }
        graded.sort(Comparator.comparingDouble(GradedAssignment::getPercent).thenComparing(
                Comparator.comparingDouble(GradedAssignment::getPointsPossible).reversed()));
        final int dropped = Math.min(dropLowest, graded.size() - 1);
        return OptionalDouble.of(averaging.average(graded.subList(dropped, graded.size())));
    }
}
