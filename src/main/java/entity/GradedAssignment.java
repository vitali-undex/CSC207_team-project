package entity;

import java.util.Objects;

/**
 * One assignment in a course's gradebook, with the student's mark once it has
 * been graded.
 */
public class GradedAssignment {

    private static final double PERCENT = 100.0;

    private final String name;
    private final Double pointsEarned;
    private final double pointsPossible;

    /**
     * Creates a graded assignment.
     *
     * @param name           the assignment's name, not null
     * @param pointsEarned   the student's mark, or null if it hasn't been graded yet
     * @param pointsPossible the most points available, more than 0
     * @throws IllegalArgumentException if pointsPossible isn't more than 0, or a number isn't
     *                                  finite
     */
    public GradedAssignment(String name, Double pointsEarned, double pointsPossible) {
        this.name = Objects.requireNonNull(name, "name");
        if (!(Double.isFinite(pointsPossible) && pointsPossible > 0)) {
            throw new IllegalArgumentException("pointsPossible must be more than 0");
        }
        if (pointsEarned != null && !Double.isFinite(pointsEarned)) {
            throw new IllegalArgumentException("pointsEarned must be a finite number");
        }
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the student's mark.
     *
     * @return the points earned, or null if the assignment hasn't been graded
     */
    public Double getPointsEarned() {
        return pointsEarned;
    }

    public double getPointsPossible() {
        return pointsPossible;
    }

    /**
     * Returns whether the assignment has a mark yet.
     *
     * @return true once the assignment has been graded
     */
    public boolean isGraded() {
        return pointsEarned != null;
    }

    /**
     * Returns the mark as a percentage of the points possible.
     *
     * @return the percentage, for example 85.0 for 17 out of 20
     * @throws IllegalStateException if the assignment hasn't been graded
     */
    public double getPercent() {
        if (!isGraded()) {
            throw new IllegalStateException("Not graded yet: " + name);
        }
        return pointsEarned / pointsPossible * PERCENT;
    }
}
