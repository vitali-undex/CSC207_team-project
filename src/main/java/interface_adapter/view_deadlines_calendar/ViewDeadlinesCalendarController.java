package interface_adapter.view_deadlines_calendar;

import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInputBoundary;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInputData;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

public final class ViewDeadlinesCalendarController {
    private final ViewDeadlinesCalendarInputBoundary interactor;
    private final CalendarViewModel viewModel;
    private final ZoneId zone;

    public ViewDeadlinesCalendarController(ViewDeadlinesCalendarInputBoundary interactor,
                                           CalendarViewModel viewModel, ZoneId zone) {
        this.interactor = interactor;
        this.viewModel = viewModel;
        this.zone = zone;
    }

    public void loadMonth(YearMonth month) {
        interactor.execute(new ViewDeadlinesCalendarInputData(month, zone));
    }

    public void selectDate(LocalDate date) {
        viewModel.selectDate(date);
    }
}
