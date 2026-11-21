package use_case.view_deadlines_calendar;

import java.io.IOException;
import java.util.Optional;

/** Local snapshots scoped to the same account as the online source. */
public interface CalendarCacheDataAccessInterface {
    Optional<CalendarSnapshot> readSnapshot() throws IOException;

    void saveSnapshot(CalendarSnapshot snapshot) throws IOException;
}
