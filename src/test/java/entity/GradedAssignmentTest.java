package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradedAssignmentTest {

    private static final double DELTA = 1e-9;

    @Test
    void gradedAssignmentKnowsItsPercent() {
        GradedAssignment quiz = new GradedAssignment("Quiz 1", 17.0, 20);

        assertEquals("Quiz 1", quiz.getName());
        assertEquals(17.0, quiz.getPointsEarned());
        assertEquals(20.0, quiz.getPointsPossible(), DELTA);
        assertTrue(quiz.isGraded());
        assertEquals(85.0, quiz.getPercent(), DELTA);
    }

    @Test
    void bonusMarksCanGoOverOneHundredPercent() {
        assertEquals(110.0, new GradedAssignment("Bonus", 11.0, 10).getPercent(), DELTA);
    }

    @Test
    void ungradedAssignmentHasNoPercent() {
        GradedAssignment quiz = new GradedAssignment("Quiz 2", null, 10);

        assertFalse(quiz.isGraded());
        assertNull(quiz.getPointsEarned());
        assertThrows(IllegalStateException.class, quiz::getPercent);
    }

    @Test
    void pointsPossibleMustBeAPositiveNumber() {
        assertThrows(IllegalArgumentException.class, () -> new GradedAssignment("Quiz", 1.0, 0));
        assertThrows(IllegalArgumentException.class, () -> new GradedAssignment("Quiz", 1.0, -5));
        assertThrows(IllegalArgumentException.class, () -> new GradedAssignment("Quiz", 1.0, Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new GradedAssignment("Quiz", 1.0, Double.POSITIVE_INFINITY));
    }

    @Test
    void pointsEarnedMustBeFinite() {
        assertThrows(IllegalArgumentException.class, () -> new GradedAssignment("Quiz", Double.NaN, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new GradedAssignment("Quiz", Double.NEGATIVE_INFINITY, 10));
    }

    @Test
    void nameIsRequired() {
        assertThrows(NullPointerException.class, () -> new GradedAssignment(null, 1.0, 10));
    }
}
