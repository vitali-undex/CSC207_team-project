package interface_adapter.view_deadlines_calendar;

import org.junit.jupiter.api.Test;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInputData;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewDeadlinesCalendarControllerTest {
    @Test
    void passesRequestedYearMonthAndZoneToInteractor() {
        LocalDate today = LocalDate.of(2027, 12, 31);
        ZoneId zone = ZoneId.of("America/Toronto");
        CalendarViewModel model = new CalendarViewModel(today);
        AtomicReference<ViewDeadlinesCalendarInputData> input = new AtomicReference<>();
        ViewDeadlinesCalendarController controller = new ViewDeadlinesCalendarController(input::set, model, zone);
        YearMonth month = YearMonth.of(2028, 1);
        controller.loadMonth(month);
        controller.selectDate(today.minusDays(1));
        assertEquals(month, input.get().getMonth());
        assertEquals(zone, input.get().getZone());
        assertEquals(today.minusDays(1), model.getSelectedDate());
    }
}
