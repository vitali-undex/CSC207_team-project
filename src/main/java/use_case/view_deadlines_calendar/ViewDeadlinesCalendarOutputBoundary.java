package use_case.view_deadlines_calendar;

import java.time.YearMonth;

public interface ViewDeadlinesCalendarOutputBoundary {
    void prepareSuccessView(ViewDeadlinesCalendarOutputData outputData);

    void prepareFailView(YearMonth month, String message);
}
