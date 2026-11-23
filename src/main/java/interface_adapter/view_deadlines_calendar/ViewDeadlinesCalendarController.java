package interface_adapter.view_deadlines_calendar;

import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInputBoundary;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInputData;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

/** Translates calendar actions into use case requests and date selections. */
public final class ViewDeadlinesCalendarController {
    private final ViewDeadlinesCalendarInputBoundary interactor;
    private final CalendarViewModel viewModel;
    private final ZoneId zone;

    /** Connects the calendar use case and view model using the display time zone. */
    public ViewDeadlinesCalendarController(ViewDeadlinesCalendarInputBoundary interactor,
                                           CalendarViewModel viewModel, ZoneId zone) {
        this.interactor = interactor;
        this.viewModel = viewModel;
        this.zone = zone;
    }

    /** Requests deadlines for the given month in the display time zone. */
    public void loadMonth(YearMonth month) {
        interactor.execute(new ViewDeadlinesCalendarInputData(month, zone));
    }

    /** Selects a date in the calendar view model. */
    public void selectDate(LocalDate date) {
        viewModel.selectDate(date);
    }
}
