package use_case.view_deadlines_calendar;

import java.io.IOException;
import java.util.Optional;

/** Local snapshots scoped to the same account as the online source. */
public interface CalendarCacheDataAccessInterface {
    /** Reads the last saved snapshot, if one exists. */
    Optional<CalendarSnapshot> readSnapshot() throws IOException;

    /** Saves the snapshot for later offline use. */
    void saveSnapshot(CalendarSnapshot snapshot) throws IOException;
}
