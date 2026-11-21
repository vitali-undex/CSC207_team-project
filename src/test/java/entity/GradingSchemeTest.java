package entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradingSchemeTest {

    private static final double DELTA = 1e-9;
    private static final AveragingStrategy POINTS = new PointsAveraging();
    private static final AveragingStrategy EQUAL = new EqualWeightAveraging();

    /**
     * Quizzes: 8/10 (80%), 3/5 (60%) and one not graded. Midterm: 34/50 (68%).
     * Final exam: not graded.
     */
    private static Gradebook gradebook() {
        return new Gradebook("course-1", "Course One", List.of(
                new AssignmentGroup("quizzes", "Quizzes", List.of(
                        new GradedAssignment("Quiz 1", 8.0, 10),
                        new GradedAssignment("Quiz 2", 3.0, 5),
                        new GradedAssignment("Quiz 3", null, 10))),
                new AssignmentGroup("midterm", "Midterm", List.of(new GradedAssignment("Midterm", 34.0, 50))),
                new AssignmentGroup("final", "Final exam", List.of(new GradedAssignment("Final exam", null, 100)))));
    }

    private static GradingScheme scheme(GroupWeight... weights) {
        return new GradingScheme("course-1", List.of(weights));
    }

    @Test
    void addsUpItsWeights() {
        GradingScheme scheme = scheme(new GroupWeight("quizzes", 20, POINTS, 0, 0, false),
                new GroupWeight("midterm", 30, POINTS, 0, 0, false), new GroupWeight("final", 50, POINTS, 0, 0, false));

        assertEquals("course-1", scheme.getCourseId());
        assertEquals(3, scheme.getWeights().size());
        assertEquals(100.0, scheme.getTotalWeight(), DELTA);
        assertTrue(scheme.isComplete());
    }

    @Test
    void acceptsRoundedWeightsThatAddUpToOneHundred() {
        assertTrue(scheme(new GroupWeight("quizzes", 33.33, POINTS, 0, 0, false), new GroupWeight("midterm", 33.33, POINTS, 0, 0, false),
                new GroupWeight("final", 33.34, POINTS, 0, 0, false)).isComplete());
    }

    @Test
    void isIncompleteUnlessTheWeightsAddUpToOneHundred() {
        assertFalse(scheme(new GroupWeight("quizzes", 20, POINTS, 0, 0, false), new GroupWeight("final", 70, POINTS, 0, 0, false))
                .isComplete());
        assertFalse(scheme(new GroupWeight("quizzes", 40, POINTS, 0, 0, false), new GroupWeight("final", 70, POINTS, 0, 0, false))
                .isComplete());
        assertFalse(scheme().isComplete());
    }

    @Test
    void findsTheWeightForAGroup() {
        GradingScheme scheme = scheme(new GroupWeight("midterm", 30, POINTS, 0, 0, false));

        assertEquals(30.0, scheme.weightFor("midterm").get().getWeightPercent(), DELTA);
        assertTrue(scheme.weightFor("labs").isEmpty());
    }

    @Test
    void weightsCantBeChangedFromOutside() {
        List<GroupWeight> weights = new ArrayList<>();
        weights.add(new GroupWeight("midterm", 30, POINTS, 0, 0, false));
        GradingScheme scheme = new GradingScheme("course-1", weights);

        weights.clear();

        assertEquals(1, scheme.getWeights().size());
        assertThrows(UnsupportedOperationException.class, () -> scheme.getWeights().clear());
        assertThrows(NullPointerException.class, () -> new GradingScheme(null, List.of()));
    }

    @Test
    void gradeCountsOnlyTheWorkGradedSoFar() {
        GradingScheme scheme = scheme(new GroupWeight("quizzes", 20, POINTS, 0, 0, false),
                new GroupWeight("midterm", 30, POINTS, 0, 0, false), new GroupWeight("final", 50, POINTS, 0, 0, false));
        double quizzes = 11.0 / 15 * 100;

        assertEquals((quizzes * 20 + 68.0 * 30) / 50, scheme.weightedGrade(gradebook()).getAsDouble(), DELTA);
        assertEquals(50.0, scheme.gradedWeight(gradebook()), DELTA);
    }

    @Test
    void gradeUsesEachGroupsDropsAndAveraging() {
        GradingScheme scheme = scheme(new GroupWeight("quizzes", 20, EQUAL, 1, 0, false),
                new GroupWeight("midterm", 30, POINTS, 0, 0, false), new GroupWeight("final", 50, POINTS, 0, 0, false));

        assertEquals((80.0 * 20 + 68.0 * 30) / 50, scheme.weightedGrade(gradebook()).getAsDouble(), DELTA);
    }

    @Test
    void groupsWithNoWeightDontCount() {
        GradingScheme scheme = scheme(new GroupWeight("quizzes", 0, POINTS, 0, 0, false),
                new GroupWeight("midterm", 100, POINTS, 0, 0, false));

        assertEquals(68.0, scheme.weightedGrade(gradebook()).getAsDouble(), DELTA);
        assertEquals(100.0, scheme.gradedWeight(gradebook()), DELTA);
    }

    @Test
    void hasNoGradeUntilWeightedWorkIsGraded() {
        GradingScheme scheme = scheme(new GroupWeight("final", 100, POINTS, 0, 0, false));

        assertTrue(scheme.weightedGrade(gradebook()).isEmpty());
        assertEquals(0.0, scheme.gradedWeight(gradebook()), DELTA);
        assertTrue(scheme().weightedGrade(gradebook()).isEmpty());
    }
}
