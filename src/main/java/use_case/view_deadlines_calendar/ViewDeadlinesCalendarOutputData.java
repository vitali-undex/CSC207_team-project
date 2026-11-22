package use_case.view_deadlines_calendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/** Contains a month of deadlines and information about when and how they were loaded. */
public final class ViewDeadlinesCalendarOutputData {
    /** Identifies whether the data came from Canvas or the cache and whether it was saved. */
    public enum Source {
        LIVE, CACHED, LIVE_WITHOUT_BACKUP
    }

    private final YearMonth month;
    private final Map<LocalDate, List<DeadlineSummary>> deadlines;
    private final ZonedDateTime syncedAt;
    private final Source source;

    /** Creates a result with deadlines grouped by date, the sync time, and the data source. */
    public ViewDeadlinesCalendarOutputData(
            YearMonth month, Map<LocalDate, List<DeadlineSummary>> deadlines,
            ZonedDateTime syncedAt, Source source) {
        this.month = month;
        this.deadlines = Map.copyOf(deadlines);
        this.syncedAt = syncedAt;
        this.source = source;
    }

    public YearMonth getMonth() {
        return month;
    }

    public Map<LocalDate, List<DeadlineSummary>> getDeadlines() {
        return deadlines;
    }

    public ZonedDateTime getSyncedAt() {
        return syncedAt;
    }

    public Source getSource() {
        return source;
    }
}
