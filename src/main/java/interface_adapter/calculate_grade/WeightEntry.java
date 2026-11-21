package interface_adapter.calculate_grade;

/**
 * What the student typed in one row of the grade calculator's table.
 */
public class WeightEntry {

    private final String groupId;
    private final String weightText;
    private final String dropText;
    private final String averagingId;

    /**
     * Creates an entry.
     *
     * @param groupId     the assignment group's id
     * @param weightText  the weight as typed, for example "20" or "20%"
     * @param dropText    the drop count as typed, blank for none
     * @param averagingId the id of the averaging rule the student picked
     */
    public WeightEntry(String groupId, String weightText, String dropText, String averagingId) {
        this.groupId = groupId;
        this.weightText = weightText;
        this.dropText = dropText;
        this.averagingId = averagingId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getWeightText() {
        return weightText;
    }

    public String getDropText() {
        return dropText;
    }

    public String getAveragingId() {
        return averagingId;
    }
}
