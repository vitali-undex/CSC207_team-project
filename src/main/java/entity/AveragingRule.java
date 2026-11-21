package entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A syllabus rule, such as "drop the lowest mark", wrapped around another
 * averaging strategy (the Decorator pattern). A rule is an averaging strategy
 * itself, so rules stack: a cap can wrap a drop, which wraps "by points". Each
 * rule changes the marks going in or the average coming out, and leaves the
 * rest to the strategy it wraps, so no class has to know every combination.
 */
public abstract class AveragingRule implements AveragingStrategy {

    private final AveragingStrategy inner;

    /**
     * Wraps a strategy in this rule.
     *
     * @param inner the strategy this rule adds to, not null
     */
    protected AveragingRule(AveragingStrategy inner) {
        this.inner = Objects.requireNonNull(inner, "inner");
    }

    /**
     * Returns the id of the averaging this rule is built on.
     *
     * @return the wrapped strategy's id, for example "points"
     */
    @Override
    public String getId() {
        return inner.getId();
    }

    /**
     * Returns the wrapped strategy's name with this rule added.
     *
     * @return the name, for example "By points, drop lowest 1"
     */
    @Override
    public String getName() {
        return inner.getName() + ", " + describe();
    }

    /**
     * Describes this rule on its own.
     *
     * @return the description, for example "drop lowest 1"
     */
    protected abstract String describe();

    /**
     * Averages marks with the strategy this rule wraps.
     *
     * @param graded the marks to average, at least one
     * @return the wrapped strategy's average
     */
    protected double averageInner(List<GradedAssignment> graded) {
        return inner.average(graded);
    }

    /**
     * Sorts marks from the lowest percentage to the highest. Of two equal
     * percentages, the one worth more points comes first, so rules that leave
     * out low marks leave it out first, which helps the student more.
     *
     * @param graded the marks to sort
     * @return a sorted copy
     */
    protected static List<GradedAssignment> lowestFirst(List<GradedAssignment> graded) {
        final List<GradedAssignment> sorted = new ArrayList<>(graded);
        sorted.sort(Comparator.comparingDouble(GradedAssignment::getPercent).thenComparing(
                Comparator.comparingDouble(GradedAssignment::getPointsPossible).reversed()));
        return sorted;
    }
}
