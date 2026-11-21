package entity;

import java.util.List;

/**
 * Averages a group by points: the total points earned over the total points
 * possible, so an assignment out of 50 counts five times as much as one out
 * of 10. This is how Quercus averages a group.
 */
public class PointsAveraging implements AveragingStrategy {

    /** The id saved with grading schemes that use this rule. */
    public static final String ID = "points";

    private static final double PERCENT = 100.0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "By points";
    }

    @Override
    public double average(List<GradedAssignment> graded) {
        double earned = 0;
        double possible = 0;
        for (GradedAssignment assignment : graded) {
            earned += assignment.getPointsEarned();
            possible += assignment.getPointsPossible();
        }
        return earned / possible * PERCENT;
    }
}
