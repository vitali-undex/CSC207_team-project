package entity;

import java.util.Objects;

/**
 * What the syllabus says about one assignment group: how much it counts toward
 * the course grade, how many of its lowest marks are dropped, and how its
 * marks are averaged.
 */
public class GroupWeight {

    private static final double MAX_WEIGHT = 100.0;

    private final String groupId;
    private final double weightPercent;
    private final int dropLowest;
    private final AveragingStrategy averaging;

    /**
     * Creates a group weight.
     *
     * @param groupId       the assignment group's id, not null
     * @param weightPercent the group's share of the course grade, from 0 to 100
     * @param dropLowest    how many of the group's lowest marks to drop, 0 or more
     * @param averaging     how the group's marks are averaged, not null
     * @throws IllegalArgumentException if the weight or drop count is out of range
     */
    public GroupWeight(String groupId, double weightPercent, int dropLowest,
                       AveragingStrategy averaging) {
        this.groupId = Objects.requireNonNull(groupId, "groupId");
        this.averaging = Objects.requireNonNull(averaging, "averaging");
        if (!(weightPercent >= 0 && weightPercent <= MAX_WEIGHT)) {
            throw new IllegalArgumentException("weightPercent must be between 0 and 100");
        }
        if (dropLowest < 0) {
            throw new IllegalArgumentException("dropLowest must not be negative");
        }
        this.weightPercent = weightPercent;
        this.dropLowest = dropLowest;
    }

    public String getGroupId() {
        return groupId;
    }

    public double getWeightPercent() {
        return weightPercent;
    }

    public int getDropLowest() {
        return dropLowest;
    }

    public AveragingStrategy getAveraging() {
        return averaging;
    }
}
