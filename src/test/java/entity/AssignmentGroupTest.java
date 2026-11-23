package entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssignmentGroupTest {

    private static final double DELTA = 1e-9;
    private static final AveragingStrategy POINTS = new PointsAveraging();
    private static final AveragingStrategy EQUAL = new EqualWeightAveraging();

    /** 80%, 60% and 90%, plus a quiz that isn't graded yet. */
    private static AssignmentGroup quizzes() {
        return new AssignmentGroup("quizzes", "Quizzes", List.of(
                new GradedAssignment("Quiz 1", 8.0, 10),
                new GradedAssignment("Quiz 2", 3.0, 5),
                new GradedAssignment("Quiz 3", 18.0, 20),
                new GradedAssignment("Quiz 4", null, 10)));
    }

    @Test
    void knowsItsAssignmentsAndHowManyAreGraded() {
        AssignmentGroup group = quizzes();

        assertEquals("quizzes", group.getId());
        assertEquals("Quizzes", group.getName());
        assertEquals(4, group.getAssignments().size());
        assertEquals(3, group.getGradedCount());
    }

    @Test
    void averagesByPointsSkippingUngradedWork() {
        assertEquals(29.0 / 35 * 100, quizzes().average(POINTS).getAsDouble(), DELTA);
    }

    @Test
    void averagesEquallyWhenTheSyllabusSaysSo() {
        assertEquals((80.0 + 60.0 + 90.0) / 3, quizzes().average(EQUAL).getAsDouble(), DELTA);
    }

    @Test
    void givesItsRuleOnlyTheGradedMarks() {
        assertEquals(26.0 / 30 * 100, quizzes().average(new DropLowest(POINTS, 1)).getAsDouble(), DELTA);
    }

    @Test
    void hasNoAverageUntilSomethingIsGraded() {
        AssignmentGroup group = new AssignmentGroup("final", "Final exam",
                List.of(new GradedAssignment("Final exam", null, 100)));

        assertEquals(0, group.getGradedCount());
        assertTrue(group.average(POINTS).isEmpty());
    }

    @Test
    void needsARule() {
        assertThrows(NullPointerException.class, () -> quizzes().average(null));
    }

    @Test
    void assignmentsCantBeChangedFromOutside() {
        List<GradedAssignment> assignments = new ArrayList<>();
        assignments.add(new GradedAssignment("Quiz 1", 8.0, 10));
        AssignmentGroup group = new AssignmentGroup("quizzes", "Quizzes", assignments);

        assignments.add(new GradedAssignment("Quiz 2", 3.0, 5));

        assertEquals(1, group.getAssignments().size());
        assertThrows(UnsupportedOperationException.class, () -> group.getAssignments().clear());
    }

    @Test
    void idAndNameAreRequired() {
        assertThrows(NullPointerException.class, () -> new AssignmentGroup(null, "Quizzes", List.of()));
        assertThrows(NullPointerException.class, () -> new AssignmentGroup("quizzes", null, List.of()));
    }
}
