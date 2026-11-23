package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GroupWeightTest {

    private static final AveragingStrategy POINTS = new PointsAveraging();

    @Test
    void holdsWhatTheSyllabusSays() {
        GroupWeight weight = new GroupWeight("quizzes", 20, POINTS, 1, 0, true);

        assertEquals("quizzes", weight.getGroupId());
        assertEquals(20.0, weight.getWeightPercent());
        assertSame(POINTS, weight.getAveraging());
        assertEquals(1, weight.getDropLowest());
        assertEquals(0, weight.getKeepBest());
        assertTrue(weight.isCapAtFullMarks());
    }

    @Test
    void acceptsWeightsFromZeroToOneHundred() {
        assertEquals(0.0, new GroupWeight("bonus", 0, POINTS, 0, 0, false).getWeightPercent());
        assertEquals(100.0, new GroupWeight("final", 100, POINTS, 0, 0, false).getWeightPercent());
    }

    @Test
    void rejectsWeightsOutsideZeroToOneHundred() {
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("q", -0.5, POINTS, 0, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("q", 100.5, POINTS, 0, 0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new GroupWeight("q", Double.NaN, POINTS, 0, 0, false));
    }

    @Test
    void rejectsNegativeRuleNumbers() {
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("q", 20, POINTS, -1, 0, false));
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("q", 20, POINTS, 0, -1, false));
    }

    @Test
    void cantBothDropTheLowestAndCountTheBest() {
        assertThrows(IllegalArgumentException.class, () -> new GroupWeight("q", 20, POINTS, 1, 8, false));
    }

    @Test
    void groupIdAndAveragingAreRequired() {
        assertThrows(NullPointerException.class, () -> new GroupWeight(null, 20, POINTS, 0, 0, false));
        assertThrows(NullPointerException.class, () -> new GroupWeight("q", 20, null, 0, 0, false));
    }

    @Test
    void withoutRulesTheRuleIsJustTheAveraging() {
        assertSame(POINTS, new GroupWeight("q", 20, POINTS, 0, 0, false).getRule());
    }

    @Test
    void wrapsTheAveragingInEachOfItsRules() {
        AveragingStrategy dropAndCap = new GroupWeight("q", 20, POINTS, 1, 0, true).getRule();
        AveragingStrategy countBest = new GroupWeight("q", 20, POINTS, 0, 3, false).getRule();

        assertTrue(dropAndCap instanceof CapAtFullMarks);
        assertEquals("By points, drop lowest 1, capped at 100%", dropAndCap.getName());
        assertTrue(countBest instanceof KeepBest);
        assertFalse(countBest instanceof CapAtFullMarks);
        assertEquals("By points, count best 3", countBest.getName());
    }
}
