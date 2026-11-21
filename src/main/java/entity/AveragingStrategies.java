package entity;

import java.util.List;
import java.util.Optional;

/**
 * The averaging rules the grade calculator offers. Adding a rule means writing
 * one {@link AveragingStrategy} class and listing it here.
 */
public final class AveragingStrategies {

    private static final List<AveragingStrategy> ALL =
            List.of(new PointsAveraging(), new EqualWeightAveraging());

    private AveragingStrategies() {
    }

    /**
     * Returns every rule, the default first.
     *
     * @return the rules, which can't be changed
     */
    public static List<AveragingStrategy> all() {
        return ALL;
    }

    /**
     * Returns the rule used when the student hasn't picked one: by points,
     * the way Quercus averages.
     *
     * @return the default rule
     */
    public static AveragingStrategy defaultStrategy() {
        return ALL.get(0);
    }

    /**
     * Finds a rule by its id.
     *
     * @param id the rule's id, may be null
     * @return the rule, or empty if no rule has that id
     */
    public static Optional<AveragingStrategy> byId(String id) {
        for (AveragingStrategy strategy : ALL) {
            if (strategy.getId().equals(id)) {
                return Optional.of(strategy);
            }
        }
        return Optional.empty();
    }
}
