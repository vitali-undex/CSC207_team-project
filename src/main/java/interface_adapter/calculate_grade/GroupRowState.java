package interface_adapter.calculate_grade;

/**
 * One row of the grade calculator's table, ready to show.
 */
public class GroupRowState {

    private final String groupId;
    private final String name;
    private final String weightText;
    private final Choice averaging;
    private final String dropText;
    private final String keepBestText;
    private final boolean capAtFullMarks;
    private final String averageText;
    private final String gradedText;

    /**
     * Creates a row.
     *
     * @param groupId        the assignment group's id
     * @param name           the group's name
     * @param weightText     the group's weight, for example "20"
     * @param averaging      how the group's marks are averaged
     * @param dropText       how many lowest marks are dropped, for example "1"
     * @param keepBestText   how many best marks count, blank for all
     * @param capAtFullMarks true if the group's average can't go above 100%
     * @param averageText    the group's average, for example "86.67%", or a dash if nothing is
     *                       graded
     * @param gradedText     how much of the group is graded, for example "3 of 4"
     */
    public GroupRowState(String groupId, String name, String weightText, Choice averaging,
                         String dropText, String keepBestText, boolean capAtFullMarks,
                         String averageText, String gradedText) {
        this.groupId = groupId;
        this.name = name;
        this.weightText = weightText;
        this.averaging = averaging;
        this.dropText = dropText;
        this.keepBestText = keepBestText;
        this.capAtFullMarks = capAtFullMarks;
        this.averageText = averageText;
        this.gradedText = gradedText;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getWeightText() {
        return weightText;
    }

    public Choice getAveraging() {
        return averaging;
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

    public String getAverageText() {
        return averageText;
    }

    public String getGradedText() {
        return gradedText;
    }
}
