package use_case.calculate_grade;

/**
 * What the student entered for one assignment group. A null number means what
 * the student typed wasn't a number.
 */
public class GroupWeightInput {

    private final String groupId;
    private final Double weightPercent;
    private final Integer dropLowest;
    private final String averagingId;

    /**
     * Creates the input for one group.
     *
     * @param groupId       the assignment group's id
     * @param weightPercent the weight the student entered, or null if it wasn't a number
     * @param dropLowest    the drop count the student entered, or null if it wasn't a whole number
     * @param averagingId   the id of the averaging rule the student picked
     */
    public GroupWeightInput(String groupId, Double weightPercent, Integer dropLowest,
                            String averagingId) {
        this.groupId = groupId;
        this.weightPercent = weightPercent;
        this.dropLowest = dropLowest;
        this.averagingId = averagingId;
    }

    public String getGroupId() {
        return groupId;
    }

    public Double getWeightPercent() {
        return weightPercent;
    }

    public Integer getDropLowest() {
        return dropLowest;
    }

    public String getAveragingId() {
        return averagingId;
    }
}
