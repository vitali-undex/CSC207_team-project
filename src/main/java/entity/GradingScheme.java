package entity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * A course's grading scheme as the student copied it from the syllabus: for
 * each assignment group, its weight, how many low marks it drops, and how its
 * marks are averaged.
 */
public class GradingScheme {

    /**
     * How far the weights may be from 100 and still count as complete, so that
     * rounded weights like 33.33 + 33.33 + 33.34 are accepted.
     */
    public static final double TOLERANCE = 0.01;

    private final String courseId;
    private final List<GroupWeight> weights;

    /**
     * Creates a grading scheme.
     *
     * @param courseId the course's id, not null
     * @param weights  one weight per assignment group, not null
     */
    public GradingScheme(String courseId, List<GroupWeight> weights) {
        this.courseId = Objects.requireNonNull(courseId, "courseId");
        this.weights = List.copyOf(weights);
    }

    public String getCourseId() {
        return courseId;
    }

    /**
     * Returns the weights in the scheme.
     *
     * @return the weights, which can't be changed
     */
    public List<GroupWeight> getWeights() {
        return weights;
    }

    /**
     * Returns the sum of all the weights.
     *
     * @return the total, which is 100 for a complete scheme
     */
    public double getTotalWeight() {
        double total = 0;
        for (GroupWeight weight : weights) {
            total += weight.getWeightPercent();
        }
        return total;
    }

    /**
     * Returns whether the weights add up to 100, give or take {@link #TOLERANCE}.
     *
     * @return true if the scheme accounts for the whole course
     */
    public boolean isComplete() {
        return Math.abs(getTotalWeight() - 100.0) <= TOLERANCE;
    }

    /**
     * Returns the weight for one assignment group.
     *
     * @param groupId the group's id
     * @return the group's weight, or empty if the scheme doesn't mention it
     */
    public Optional<GroupWeight> weightFor(String groupId) {
        for (GroupWeight weight : weights) {
            if (weight.getGroupId().equals(groupId)) {
                return Optional.of(weight);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the student's current grade, the way Quercus shows a current
     * grade: each group's average times its weight, divided by the weight of
     * the groups graded so far. Groups with no weight, or with nothing graded,
     * don't count.
     *
     * @param gradebook the course's groups and scores
     * @return the grade as a percentage, or empty if nothing weighted is graded
     */
    public OptionalDouble weightedGrade(Gradebook gradebook) {
        double weightedSum = 0;
        double countedWeight = 0;
        for (AssignmentGroup group : gradebook.getGroups()) {
            final Optional<GroupWeight> weight = weightFor(group.getId());
            if (weight.isPresent() && weight.get().getWeightPercent() > 0) {
                final OptionalDouble average =
                        group.average(weight.get().getDropLowest(), weight.get().getAveraging());
                if (average.isPresent()) {
                    weightedSum += average.getAsDouble() * weight.get().getWeightPercent();
                    countedWeight += weight.get().getWeightPercent();
                }
            }
        }
        if (countedWeight == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(weightedSum / countedWeight);
    }

    /**
     * Returns how much of the course has been graded so far: the total weight
     * of the groups that have at least one score.
     *
     * @param gradebook the course's groups and scores
     * @return the graded weight, for example 45.0 when 45% of the course is graded
     */
    public double gradedWeight(Gradebook gradebook) {
        double graded = 0;
        for (AssignmentGroup group : gradebook.getGroups()) {
            final Optional<GroupWeight> weight = weightFor(group.getId());
            if (weight.isPresent() && group.getGradedCount() > 0) {
                graded += weight.get().getWeightPercent();
            }
        }
        return graded;
    }
}
