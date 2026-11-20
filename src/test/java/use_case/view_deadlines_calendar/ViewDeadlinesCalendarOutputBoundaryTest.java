package use_case.view_deadlines_calendar;

import interface_adapter.view_deadlines_calendar.CalendarViewModel;
import interface_adapter.view_deadlines_calendar.ViewDeadlinesCalendarPresenter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ViewDeadlinesCalendarOutputBoundaryTest {
    @Test
    void presenterPublishesFailureForTheRequestedMonth() {
        final LocalDate selected = LocalDate.of(2028, 2, 29);
        final CalendarViewModel model = new CalendarViewModel(selected);
        final ViewDeadlinesCalendarOutputBoundary output =
                new ViewDeadlinesCalendarPresenter(model, Runnable::run);
        model.beginLoad(selected);
        output.prepareFailView(YearMonth.from(selected), "Quercus unavailable");
        assertFalse(model.isLoading());
        assertEquals("Quercus unavailable", model.getStatus());
    }
}
