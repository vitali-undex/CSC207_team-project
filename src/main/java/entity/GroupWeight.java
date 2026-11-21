package entity;

import java.util.Objects;

/**
 * What the syllabus says about one assignment group: how much it counts toward
 * the course grade, how its marks are averaged, and its rules (drop the lowest
 * marks or count only the best ones, and cap bonus marks at 100%).
 */
public class GroupWeight {

    private static final double MAX_WEIGHT = 100.0;

    private final String groupId;
    private final double weightPercent;
    private final AveragingStrategy averaging;
    private final int dropLowest;
    private final int keepBest;
    private final boolean capAtFullMarks;

    /**
     * Creates a group weight.
     *
     * @param groupId        the assignment group's id, not null
     * @param weightPercent  the group's share of the course grade, from 0 to 100
     * @param averaging      how the group's marks are averaged, not null
     * @param dropLowest     how many of the lowest marks to drop, 0 for none
     * @param keepBest       how many of the best marks count, 0 for all of them
     * @param capAtFullMarks true if the group's average can't go above 100%
     * @throws IllegalArgumentException if a number is out of range, or the group both drops
     *                                  its lowest marks and counts only its best ones
     */
    public GroupWeight(String groupId, double weightPercent, AveragingStrategy averaging,
                       int dropLowest, int keepBest, boolean capAtFullMarks) {
        this.groupId = Objects.requireNonNull(groupId, "groupId");
        this.averaging = Objects.requireNonNull(averaging, "averaging");
        if (!(weightPercent >= 0 && weightPercent <= MAX_WEIGHT)) {
            throw new IllegalArgumentException("weightPercent must be between 0 and 100");
        }
        if (dropLowest < 0 || keepBest < 0) {
            throw new IllegalArgumentException("dropLowest and keepBest must not be negative");
        }
        if (dropLowest > 0 && keepBest > 0) {
            throw new IllegalArgumentException(
                    "a group can't both drop its lowest marks and keep its best");
        }
        this.weightPercent = weightPercent;
        this.dropLowest = dropLowest;
        this.keepBest = keepBest;
        this.capAtFullMarks = capAtFullMarks;
    }

    public String getGroupId() {
        return groupId;
    }

    public double getWeightPercent() {
        return weightPercent;
    }

    /**
     * Returns how the group's marks are averaged, without its rules.
     *
     * @return the averaging strategy, for example by points
     */
    public AveragingStrategy getAveraging() {
        return averaging;
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

    /**
     * Returns the averaging with this group's rules wrapped around it: first
     * the rule that picks which marks count, then the cap.
     *
     * @return the strategy that averages this group
     */
    public AveragingStrategy getRule() {
        AveragingStrategy rule = averaging;
        if (keepBest > 0) {
            rule = new KeepBest(rule, keepBest);
        }
        else if (dropLowest > 0) {
            rule = new DropLowest(rule, dropLowest);
        }
        if (capAtFullMarks) {
            rule = new CapAtFullMarks(rule);
        }
        return rule;
    }
}
