package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AveragingRulesTest {

    private static final double DELTA = 1e-9;
    private static final AveragingStrategy POINTS = new PointsAveraging();
    private static final AveragingStrategy EQUAL = new EqualWeightAveraging();

    /** 8/10 (80%), 3/5 (60%) and 18/20 (90%). */
    private static final List<GradedAssignment> MARKS = List.of(
            new GradedAssignment("Quiz 1", 8.0, 10),
            new GradedAssignment("Quiz 2", 3.0, 5),
            new GradedAssignment("Quiz 3", 18.0, 20));

    @Test
    void dropLowestLeavesOutTheLowestPercentages() {
        assertEquals(26.0 / 30 * 100, new DropLowest(POINTS, 1).average(MARKS), DELTA);
        assertEquals(85.0, new DropLowest(EQUAL, 1).average(MARKS), DELTA);
    }

    @Test
    void dropLowestAlwaysKeepsOneMark() {
        assertEquals(90.0, new DropLowest(POINTS, 10).average(MARKS), DELTA);
    }

    @Test
    void dropLowestDropsTheBiggerOfTwoEqualPercentages() {
        List<GradedAssignment> labs = List.of(
                new GradedAssignment("Lab 1", 5.0, 10),
                new GradedAssignment("Lab 2", 10.0, 20),
                new GradedAssignment("Lab 3", 18.0, 20));

        assertEquals(23.0 / 30 * 100, new DropLowest(POINTS, 1).average(labs), DELTA);
    }

    @Test
    void countBestLeavesNothingOutUntilMoreAreGraded() {
        assertEquals(29.0 / 35 * 100, new KeepBest(POINTS, 3).average(MARKS), DELTA);
        assertEquals(29.0 / 35 * 100, new KeepBest(POINTS, 5).average(MARKS), DELTA);
    }

    @Test
    void countBestKeepsTheHighestPercentages() {
        assertEquals(85.0, new KeepBest(EQUAL, 2).average(MARKS), DELTA);
        assertEquals(90.0, new KeepBest(POINTS, 1).average(MARKS), DELTA);
    }

    @Test
    void capStopsBonusMarksGoingOverOneHundred() {
        List<GradedAssignment> bonus = List.of(new GradedAssignment("Lab 1", 11.0, 10));

        assertEquals(110.0, POINTS.average(bonus), DELTA);
        assertEquals(100.0, new CapAtFullMarks(POINTS).average(bonus), DELTA);
        assertEquals(29.0 / 35 * 100, new CapAtFullMarks(POINTS).average(MARKS), DELTA);
    }

    @Test
    void rulesStack() {
        List<GradedAssignment> marks = List.of(
                new GradedAssignment("Quiz 1", 3.0, 5),
                new GradedAssignment("Quiz 2", 12.0, 10),
                new GradedAssignment("Quiz 3", 10.0, 10));
        AveragingStrategy dropThenCap = new CapAtFullMarks(new DropLowest(POINTS, 1));

        assertEquals(110.0, new DropLowest(POINTS, 1).average(marks), DELTA);
        assertEquals(100.0, dropThenCap.average(marks), DELTA);
    }

    @Test
    void rulesDescribeThemselvesOnTopOfWhatTheyWrap() {
        AveragingStrategy rule = new CapAtFullMarks(new DropLowest(EQUAL, 1));

        assertEquals("Equally, drop lowest 1, capped at 100%", rule.getName());
        assertEquals("equal", rule.getId());
        assertEquals("By points, count best 8", new KeepBest(POINTS, 8).getName());
    }

    @Test
    void rulesNeedACountOfAtLeastOneAndSomethingToWrap() {
        assertThrows(IllegalArgumentException.class, () -> new DropLowest(POINTS, 0));
        assertThrows(IllegalArgumentException.class, () -> new KeepBest(POINTS, 0));
        assertThrows(NullPointerException.class, () -> new CapAtFullMarks(null));
    }
}
