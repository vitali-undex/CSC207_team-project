package use_case.view_deadlines_calendar;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewDeadlinesCalendarInputDataTest {
    @Test
    void preservesTheRequestedYearMonthAndDisplayZone() {
        final YearMonth month = YearMonth.of(2028, 2);
        final ZoneId zone = ZoneId.of("America/Toronto");
        final ViewDeadlinesCalendarInputData input = new ViewDeadlinesCalendarInputData(month, zone);
        assertEquals(month, input.getMonth());
        assertEquals(zone, input.getZone());
    }
}
