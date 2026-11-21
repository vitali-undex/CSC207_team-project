package app;

import interface_adapter.view_deadlines_calendar.CalendarViewModel;
import interface_adapter.view_deadlines_calendar.ViewDeadlinesCalendarController;
import interface_adapter.view_deadlines_calendar.ViewDeadlinesCalendarPresenter;
import use_case.view_deadlines_calendar.CalendarAssignmentDataAccessInterface;
import use_case.view_deadlines_calendar.CalendarCacheDataAccessInterface;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarInteractor;
import view.CalendarView;

import javax.swing.SwingUtilities;
import java.time.Clock;
import java.time.LocalDate;

/** Composes the calendar for either a standalone window or a tab in the main app. */
public final class CalendarAppBuilder {
    public CalendarView buildPreview(Clock clock) {
        final CalendarViewModel viewModel = new CalendarViewModel(LocalDate.now(clock));
        final ViewDeadlinesCalendarController controller =
                new ViewDeadlinesCalendarController(input -> { }, viewModel, clock.getZone());
        return new CalendarView(viewModel, controller, clock, false);
    }

    public CalendarView build(CalendarAssignmentDataAccessInterface online,
                              CalendarCacheDataAccessInterface cache, Clock clock) {
        final CalendarViewModel viewModel = new CalendarViewModel(LocalDate.now(clock));
        final ViewDeadlinesCalendarPresenter presenter =
                new ViewDeadlinesCalendarPresenter(viewModel, SwingUtilities::invokeLater);
        final ViewDeadlinesCalendarInteractor interactor = new ViewDeadlinesCalendarInteractor(online, cache, presenter);
        final ViewDeadlinesCalendarController controller =
                new ViewDeadlinesCalendarController(interactor, viewModel, clock.getZone());
        return new CalendarView(viewModel, controller, clock, true);
    }
}
