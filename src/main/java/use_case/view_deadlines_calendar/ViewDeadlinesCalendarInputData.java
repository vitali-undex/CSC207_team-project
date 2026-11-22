package use_case.view_deadlines_calendar;

import java.time.YearMonth;
import java.time.ZoneId;

/** Specifies the month and time zone for a calendar request. */
public final class ViewDeadlinesCalendarInputData {
    private final YearMonth month;
    private final ZoneId zone;

    /** Creates a request for the given month and display time zone. */
    public ViewDeadlinesCalendarInputData(YearMonth month, ZoneId zone) {
        this.month = month;
        this.zone = zone;
    }

    public YearMonth getMonth() {
        return month;
    }

    public ZoneId getZone() {
        return zone;
    }
}
