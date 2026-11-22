package interface_adapter.view_deadlines_calendar;

import org.junit.jupiter.api.Test;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarOutputData;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewDeadlinesCalendarPresenterTest {
    @Test
    void publishesCachedEmptyMonthThroughUiExecutor() {
        LocalDate selected = LocalDate.of(2028, 1, 1);
        YearMonth month = YearMonth.from(selected);
        CalendarViewModel model = new CalendarViewModel(selected);
        Queue<Runnable> ui = new ArrayDeque<>();
        ViewDeadlinesCalendarPresenter presenter =
                new ViewDeadlinesCalendarPresenter(model, ui::add);
        model.beginLoad(selected);
        presenter.prepareSuccessView(new ViewDeadlinesCalendarOutputData(month, Map.of(),
                ZonedDateTime.parse("2027-12-31T12:00:00-05:00[America/Toronto]"),
                ViewDeadlinesCalendarOutputData.Source.CACHED));
        assertTrue(model.isLoading());
        ui.remove().run();
        assertFalse(model.isLoading());
        assertTrue(model.getStatus().contains("No deadlines this month"));
        assertTrue(model.getStatus().contains("Showing saved deadlines"));
        presenter.prepareFailView(month, "Unavailable");
        ui.remove().run();
        assertEquals("Unavailable", model.getStatus());
    }
}
