package use_case.calculate_grade;

/**
 * One assignment group's row in the grade breakdown.
 */
public class GroupResult {

    private final String groupId;
    private final String name;
    private final GroupSettings settings;
    private final int gradedCount;
    private final int assignmentCount;
    private final Double averagePercent;

    /**
     * Creates a group's row.
     *
     * @param groupId         the group's id
     * @param name            the group's name
     * @param settings        the group's weight and rules
     * @param gradedCount     how many of its assignments are graded
     * @param assignmentCount how many assignments it has
     * @param averagePercent  the group's average under its rules, or null if nothing is graded
     */
    public GroupResult(String groupId, String name, GroupSettings settings, int gradedCount,
                       int assignmentCount, Double averagePercent) {
        this.groupId = groupId;
        this.name = name;
        this.settings = settings;
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

    public GroupSettings getSettings() {
        return settings;
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
     * @return the average under the group's rules as a percentage, or null if nothing is graded
     */
    public Double getAveragePercent() {
        return averagePercent;
    }
}
