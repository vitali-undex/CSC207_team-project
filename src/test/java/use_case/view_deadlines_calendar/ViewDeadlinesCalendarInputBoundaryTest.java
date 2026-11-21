package use_case.view_deadlines_calendar;

import interface_adapter.view_deadlines_calendar.CalendarViewModel;
import interface_adapter.view_deadlines_calendar.ViewDeadlinesCalendarPresenter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewDeadlinesCalendarInputBoundaryTest {
    @Test
    void executesTheMonthRequestThroughTheInteractor() {
        final LocalDate selected = LocalDate.of(2028, 2, 29);
        final CalendarViewModel model = new CalendarViewModel(selected);
        final CalendarCacheDataAccessInterface cache = new CalendarCacheDataAccessInterface() {
            @Override
            public Optional<CalendarSnapshot> readSnapshot() {
                return Optional.empty();
            }

            @Override
            public void saveSnapshot(CalendarSnapshot snapshot) {
            }
        };
        final ViewDeadlinesCalendarInputBoundary input = new ViewDeadlinesCalendarInteractor(
                () -> new CalendarSnapshot(List.of(), List.of(), Instant.EPOCH), cache,
                new ViewDeadlinesCalendarPresenter(model, Runnable::run));
        input.execute(new ViewDeadlinesCalendarInputData(YearMonth.from(selected), ZoneOffset.UTC));
        assertEquals(YearMonth.of(2028, 2), model.getMonth());
        assertTrue(model.getStatus().startsWith("No deadlines this month"));
    }
}
