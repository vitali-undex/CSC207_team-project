package use_case.calculate_grade;

/**
 * What the student entered for one assignment group. A null number means what
 * the student typed wasn't a number.
 */
public class GroupWeightInput {

    private final String groupId;
    private final Double weightPercent;
    private final String averagingId;
    private final Integer dropLowest;
    private final Integer keepBest;
    private final boolean capAtFullMarks;

    /**
     * Creates the input for one group.
     *
     * @param groupId        the assignment group's id
     * @param weightPercent  the weight, or null if it wasn't a number
     * @param averagingId    the id of the averaging rule the student picked
     * @param dropLowest     how many lowest marks to drop (0 for none), or null if it wasn't a
     *                       whole number
     * @param keepBest       how many best marks count (0 for all), or null if it wasn't a whole
     *                       number
     * @param capAtFullMarks true if the group's average can't go above 100%
     */
    public GroupWeightInput(String groupId, Double weightPercent, String averagingId,
                            Integer dropLowest, Integer keepBest, boolean capAtFullMarks) {
        this.groupId = groupId;
        this.weightPercent = weightPercent;
        this.averagingId = averagingId;
        this.dropLowest = dropLowest;
        this.keepBest = keepBest;
        this.capAtFullMarks = capAtFullMarks;
    }

    public String getGroupId() {
        return groupId;
    }

    public Double getWeightPercent() {
        return weightPercent;
    }

    public String getAveragingId() {
        return averagingId;
    }

    public Integer getDropLowest() {
        return dropLowest;
    }

    public Integer getKeepBest() {
        return keepBest;
    }

    public boolean isCapAtFullMarks() {
        return capAtFullMarks;
    }
}
