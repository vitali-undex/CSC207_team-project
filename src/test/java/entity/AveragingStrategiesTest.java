package entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AveragingStrategiesTest {

    private static final double DELTA = 1e-9;

    /** 8 out of 10 (80%) and 3 out of 5 (60%). */
    private static final List<GradedAssignment> MARKS = List.of(
            new GradedAssignment("Quiz 1", 8.0, 10),
            new GradedAssignment("Quiz 2", 3.0, 5));

    @Test
    void byPointsAddsUpThePoints() {
        AveragingStrategy points = new PointsAveraging();

        assertEquals(PointsAveraging.ID, points.getId());
        assertEquals("By points", points.getName());
        assertEquals(11.0 / 15 * 100, points.average(MARKS), DELTA);
    }

    @Test
    void equallyCountsEachAssignmentTheSame() {
        AveragingStrategy equal = new EqualWeightAveraging();

        assertEquals(EqualWeightAveraging.ID, equal.getId());
        assertEquals("Equally", equal.getName());
        assertEquals(70.0, equal.average(MARKS), DELTA);
    }

    @Test
    void offersByPointsFirstAsTheDefault() {
        List<AveragingStrategy> all = AveragingStrategies.all();

        assertEquals(2, all.size());
        assertEquals("points", all.get(0).getId());
        assertEquals("equal", all.get(1).getId());
        assertSame(all.get(0), AveragingStrategies.defaultStrategy());
        assertThrows(UnsupportedOperationException.class, () -> all.add(new PointsAveraging()));
    }

    @Test
    void findsRulesById() {
        assertTrue(AveragingStrategies.byId("equal").get() instanceof EqualWeightAveraging);
        assertTrue(AveragingStrategies.byId("points").get() instanceof PointsAveraging);
        assertTrue(AveragingStrategies.byId("median").isEmpty());
        assertTrue(AveragingStrategies.byId(null).isEmpty());
    }
}
