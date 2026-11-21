package use_case.view_deadlines_calendar;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeadlineSummaryTest {
    @Test
    void preservesTheZonedDeadlineAndDecimalPoints() {
        final ZonedDateTime due = ZonedDateTime.parse("2027-12-31T23:59:00-05:00[America/Toronto]");
        final BigDecimal points = new BigDecimal("12.75");
        final DeadlineSummary summary = new DeadlineSummary("CSC207", "Quiz", due, points);
        assertEquals("CSC207", summary.getCourse());
        assertEquals("Quiz", summary.getTitle());
        assertEquals(due, summary.getDueAt());
        assertEquals(points, summary.getPointsPossible());
    }
}
