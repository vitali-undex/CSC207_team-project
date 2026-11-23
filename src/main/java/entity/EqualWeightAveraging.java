package entity;

import java.util.List;

/**
 * Averages a group so that each assignment counts the same, whatever it is out
 * of: the mean of the assignments' percentages. Syllabi that say "five quizzes
 * worth 2% each" mean this.
 */
public class EqualWeightAveraging implements AveragingStrategy {

    /** The id saved with grading schemes that use this rule. */
    public static final String ID = "equal";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return "Equally";
    }

    @Override
    public double average(List<GradedAssignment> graded) {
        double total = 0;
        for (GradedAssignment assignment : graded) {
            total += assignment.getPercent();
        }
        return total / graded.size();
    }
}
