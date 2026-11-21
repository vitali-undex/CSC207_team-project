package use_case.calculate_grade;

/**
 * One assignment group's weight and rules as the scheme has them.
 */
public class GroupSettings {

    private final double weightPercent;
    private final String averagingId;
    private final int dropLowest;
    private final int keepBest;
    private final boolean capAtFullMarks;

    /**
     * Creates a group's settings.
     *
     * @param weightPercent  the group's weight, 0 if it has none yet
     * @param averagingId    the id of the rule that averages the group
     * @param dropLowest     how many lowest marks are dropped, 0 for none
     * @param keepBest       how many best marks count, 0 for all
     * @param capAtFullMarks true if the group's average can't go above 100%
     */
    public GroupSettings(double weightPercent, String averagingId, int dropLowest, int keepBest,
                         boolean capAtFullMarks) {
        this.weightPercent = weightPercent;
        this.averagingId = averagingId;
        this.dropLowest = dropLowest;
        this.keepBest = keepBest;
        this.capAtFullMarks = capAtFullMarks;
    }

    public double getWeightPercent() {
        return weightPercent;
    }

    public String getAveragingId() {
        return averagingId;
    }

    public int getDropLowest() {
        return dropLowest;
    }

    public int getKeepBest() {
        return keepBest;
    }

    public boolean isCapAtFullMarks() {
        return capAtFullMarks;
    }
}
