package entity;

import java.util.List;

/**
 * Stops bonus marks from pushing a group's average above 100%, as syllabi
 * often say ("bonus marks can't take a component over its full weight").
 */
public class CapAtFullMarks extends AveragingRule {

    private static final double FULL_MARKS = 100.0;

    /**
     * Wraps a strategy so that its average is at most 100%.
     *
     * @param inner the strategy whose average is capped, not null
     */
    public CapAtFullMarks(AveragingStrategy inner) {
        super(inner);
    }

    @Override
    public double average(List<GradedAssignment> graded) {
        return Math.min(FULL_MARKS, averageInner(graded));
    }

    @Override
    protected String describe() {
        return "capped at 100%";
    }
}
