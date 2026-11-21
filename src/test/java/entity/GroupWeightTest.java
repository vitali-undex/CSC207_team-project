package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroupWeightTest {

    private static final AveragingStrategy POINTS = new PointsAveraging();

    @Test
    void holdsWhatTheSyllabusSays() {
        GroupWeight weight = new GroupWeight("quizzes", 20, 1, POINTS);

        assertEquals("quizzes", weight.getGroupId());
        assertEquals(20.0, weight.getWeightPercent());
        assertEquals(1, weight.getDropLowest());
        assertSame(POINTS, weight.getAveraging());
    }

    @Test
    void acceptsWeightsFromZeroToOneHundred() {
        assertEquals(0.0, new GroupWeight("bonus", 0, 0, POINTS).getWeightPercent());
        assertEquals(100.0, new GroupWeight("final", 100, 0, POINTS).getWeightPercent());
    }

    @Test
    void rejectsWeightsOutsideZeroToOneHundred() {
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("quizzes", -0.5, 0, POINTS));
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("quizzes", 100.5, 0, POINTS));
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("quizzes", Double.NaN, 0, POINTS));
    }

    @Test
    void rejectsNegativeDrops() {
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("quizzes", 20, -1, POINTS));
    }

    @Test
    void groupIdAndAveragingAreRequired() {
        assertThrows(NullPointerException.class, () -> new GroupWeight(null, 20, 0, POINTS));
        assertThrows(NullPointerException.class, () -> new GroupWeight("quizzes", 20, 0, null));
    }
}
