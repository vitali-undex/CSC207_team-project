package interface_adapter.calculate_grade;

/**
 * One row of the grade calculator's table, as text ready to show.
 */
public class GroupRowState {

    private final String groupId;
    private final String name;
    private final String weightText;
    private final String dropText;
    private final Choice averaging;
    private final String averageText;
    private final String gradedText;

    /**
     * Creates a row.
     *
     * @param groupId     the assignment group's id
     * @param name        the group's name
     * @param weightText  the group's weight, for example "20"
     * @param dropText    how many low marks the group drops, for example "1"
     * @param averaging   how the group's marks are averaged
     * @param averageText the group's average, for example "86.67%", or a dash if nothing is graded
     * @param gradedText  how much of the group is graded, for example "3 of 4"
     */
    public GroupRowState(String groupId, String name, String weightText, String dropText,
                         Choice averaging, String averageText, String gradedText) {
        this.groupId = groupId;
        this.name = name;
        this.weightText = weightText;
        this.dropText = dropText;
        this.averaging = averaging;
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

    public String getDropText() {
        return dropText;
    }

    public Choice getAveraging() {
        return averaging;
    }

    public String getAverageText() {
        return averageText;
    }

    public String getGradedText() {
        return gradedText;
    }
}
