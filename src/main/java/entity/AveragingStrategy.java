package entity;

import java.util.List;

/**
 * One way of combining the marks in an assignment group into a single average.
 * Syllabi differ here: some add up the points, others count each assignment
 * equally whatever it is out of. Each way is its own class (the Strategy
 * pattern), so a new way is a new class and nothing that uses it changes.
 * Syllabus rules such as "drop the lowest mark" wrap a strategy without
 * changing it: see {@link AveragingRule}.
 */
public interface AveragingStrategy {

    /**
     * Returns a short id for this rule that never changes, used to save
     * grading schemes and restore them later.
     *
     * @return the id, for example "points"
     */
    String getId();

    /**
     * Returns the rule's name as the student sees it.
     *
     * @return the name, for example "By points"
     */
    String getName();

    /**
     * Averages the graded assignments left in a group after drops.
     *
     * @param graded the graded assignments to average, at least one
     * @return the average as a percentage
     */
    double average(List<GradedAssignment> graded);
}
