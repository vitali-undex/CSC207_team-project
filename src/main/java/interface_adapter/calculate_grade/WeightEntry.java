package interface_adapter.calculate_grade;

/**
 * What the student typed in one row of the grade calculator's table.
 */
public class WeightEntry {

    private final String groupId;
    private final String weightText;
    private final String averagingId;
    private final String dropText;
    private final String keepBestText;
    private final boolean capAtFullMarks;

    /**
     * Creates an entry.
     *
     * @param groupId        the assignment group's id
     * @param weightText     the weight as typed, for example "20" or "20%"
     * @param averagingId    the id of the averaging rule the student picked
     * @param dropText       how many lowest marks to drop, as typed; blank for none
     * @param keepBestText   how many best marks count, as typed; blank for all
     * @param capAtFullMarks true if the cap box is ticked
     */
    public WeightEntry(String groupId, String weightText, String averagingId, String dropText,
                       String keepBestText, boolean capAtFullMarks) {
        this.groupId = groupId;
        this.weightText = weightText;
        this.averagingId = averagingId;
        this.dropText = dropText;
        this.keepBestText = keepBestText;
        this.capAtFullMarks = capAtFullMarks;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getWeightText() {
        return weightText;
    }

    public String getAveragingId() {
        return averagingId;
    }

    public String getDropText() {
        return dropText;
    }

    public String getKeepBestText() {
        return keepBestText;
    }

    public boolean isCapAtFullMarks() {
        return capAtFullMarks;
    }
}
