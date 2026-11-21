package entity;

import java.util.List;

/**
 * Drops a group's lowest marks before averaging, as soon as they are graded:
 * "your lowest quiz mark is dropped". At least one mark is always kept.
 */
public class DropLowest extends AveragingRule {

    private final int count;

    /**
     * Wraps a strategy so that it drops the lowest marks first.
     *
     * @param inner the strategy that averages the marks that are left, not null
     * @param count how many marks to drop, at least 1
     * @throws IllegalArgumentException if count is less than 1
     */
    public DropLowest(AveragingStrategy inner, int count) {
        super(inner);
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }
        this.count = count;
    }

    @Override
    public double average(List<GradedAssignment> graded) {
        final List<GradedAssignment> sorted = lowestFirst(graded);
        final int dropped = Math.min(count, sorted.size() - 1);
        return averageInner(sorted.subList(dropped, sorted.size()));
    }

    @Override
    protected String describe() {
        return "drop lowest " + count;
    }
}
