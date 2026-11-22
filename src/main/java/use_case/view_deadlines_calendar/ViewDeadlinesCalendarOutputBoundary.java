package use_case.view_deadlines_calendar;

import java.time.YearMonth;

/** Receives the results of a calendar load. */
public interface ViewDeadlinesCalendarOutputBoundary {
    /** Presents the loaded deadlines and their source. */
    void prepareSuccessView(ViewDeadlinesCalendarOutputData outputData);

    /** Presents the load error for the requested month. */
    void prepareFailView(YearMonth month, String message);
}
