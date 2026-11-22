package use_case.view_deadlines_calendar;

import data_access.JsonCalendarDataAccessObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarCacheDataAccessInterfaceTest {
    @TempDir
    Path directory;

    @Test
    void distinguishesMissingSnapshotFromSuccessfulSyncWithNoCourses() throws IOException {
        final CalendarCacheDataAccessInterface cache =
                new JsonCalendarDataAccessObject(directory.resolve("deadlines.json"));
        assertTrue(cache.readSnapshot().isEmpty());
        final Instant synced = Instant.parse("2028-01-01T12:00:00Z");
        cache.saveSnapshot(new CalendarSnapshot(List.of(), List.of(), synced));
        assertEquals(synced, cache.readSnapshot().orElseThrow().getSyncedAt());
    }
}
