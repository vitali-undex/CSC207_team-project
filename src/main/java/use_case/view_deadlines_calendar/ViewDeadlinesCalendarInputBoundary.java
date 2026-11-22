package use_case.view_deadlines_calendar;

/** Defines the request to load a month of deadlines. */
public interface ViewDeadlinesCalendarInputBoundary {
    /** Loads deadlines for the requested month and time zone. */
    void execute(ViewDeadlinesCalendarInputData inputData);
}
