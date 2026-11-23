package entity;

import java.util.List;

/**
 * Counts only a group's best marks: "your best 8 of 10 quizzes count". Unlike
 * {@link DropLowest}, nothing is left out until more than that many marks are
 * graded, so the grade isn't inflated early in the term.
 */
public class KeepBest extends AveragingRule {

    private final int count;

    /**
     * Wraps a strategy so that it averages only the best marks.
     *
     * @param inner the strategy that averages the marks that count, not null
     * @param count how many of the best marks count, at least 1
     * @throws IllegalArgumentException if count is less than 1
     */
    public KeepBest(AveragingStrategy inner, int count) {
        super(inner);
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }
        this.count = count;
    }

    @Override
    public double average(List<GradedAssignment> graded) {
        final List<GradedAssignment> sorted = lowestFirst(graded);
        final int firstKept = Math.max(0, sorted.size() - count);
        return averageInner(sorted.subList(firstKept, sorted.size()));
    }

    @Override
    protected String describe() {
        return "count best " + count;
    }
}
