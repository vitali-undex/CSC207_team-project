package interface_adapter.view_deadlines_calendar;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarViewModelTest {
    @Test
    void keepsCurrentMonthWhenAnotherMonthFinishesLoading() {
        LocalDate today = LocalDate.of(2027, 12, 31);
        CalendarViewModel model = new CalendarViewModel(today);
        List<String> events = new ArrayList<>();
        model.addPropertyChangeListener(event -> events.add(event.getPropertyName()));
        LocalDate nextMonth = today.plusDays(1);
        model.beginLoad(nextMonth);
        model.showMonth(YearMonth.from(today), Map.of(), "Old request");
        model.selectDate(today);
        assertEquals(nextMonth, model.getSelectedDate());
        assertTrue(model.isLoading());

        model.showMonth(YearMonth.from(nextMonth), Map.of(), "Synced");
        model.selectDate(nextMonth.plusDays(1));
        assertEquals("Synced", model.getStatus());
        assertFalse(model.isLoading());
        assertEquals(List.of("calendar", "calendar", "selectedDate"), events);
    }
}
