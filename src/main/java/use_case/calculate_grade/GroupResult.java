package use_case.calculate_grade;

/**
 * One assignment group's row in the grade breakdown.
 */
public class GroupResult {

    private final String groupId;
    private final String name;
    private final double weightPercent;
    private final int dropLowest;
    private final String averagingId;
    private final int gradedCount;
    private final int assignmentCount;
    private final Double averagePercent;

    /**
     * Creates a group's row.
     *
     * @param groupId         the group's id
     * @param name            the group's name
     * @param weightPercent   the group's weight in the scheme, 0 if it has none yet
     * @param dropLowest      how many low marks the group drops
     * @param averagingId     the id of the rule that averages the group
     * @param gradedCount     how many of its assignments are graded
     * @param assignmentCount how many assignments it has
     * @param averagePercent  the group's average after drops, or null if nothing is graded
     */
    public GroupResult(String groupId, String name, double weightPercent, int dropLowest,
                       String averagingId, int gradedCount, int assignmentCount,
                       Double averagePercent) {
        this.groupId = groupId;
        this.name = name;
        this.weightPercent = weightPercent;
        this.dropLowest = dropLowest;
        this.averagingId = averagingId;
        this.gradedCount = gradedCount;
        this.assignmentCount = assignmentCount;
        this.averagePercent = averagePercent;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public double getWeightPercent() {
        return weightPercent;
    }

    public int getDropLowest() {
        return dropLowest;
    }

    public String getAveragingId() {
        return averagingId;
    }

    public int getGradedCount() {
        return gradedCount;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    /**
     * Returns the group's average.
     *
     * @return the average after drops as a percentage, or null if nothing is graded
     */
    public Double getAveragePercent() {
        return averagePercent;
    }
}
