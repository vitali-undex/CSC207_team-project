package data_access;

import org.junit.jupiter.api.Test;
import entity.Assignment;
import entity.Course;
import org.junit.jupiter.api.io.TempDir;
import use_case.view_deadlines_calendar.CalendarSnapshot;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonCalendarDataAccessObjectTest {
    @TempDir
    Path directory;

    @Test
    void reopensSnapshotAndReplacesItAfterAnotherSync() throws IOException {
        Path file = directory.resolve("account").resolve("calendar.json");
        JsonCalendarDataAccessObject cache = new JsonCalendarDataAccessObject(file);
        assertTrue(cache.readSnapshot().isEmpty());
        Instant due = Instant.parse("2027-01-01T01:00:00Z");
        cache.saveSnapshot(new CalendarSnapshot(List.of(new Course("1", "CSC207", "Design")),
                List.of(new Assignment("2", "1", "Quiz", due, new BigDecimal("12.5"))), due));

        CalendarSnapshot restored =
            new JsonCalendarDataAccessObject(file).readSnapshot().orElseThrow();
        assertEquals("CSC207", restored.getCourses().get(0).getCode());
        assertEquals(due, restored.getAssignments().get(0).getDueAt());
        assertEquals(new BigDecimal("12.5"), restored.getAssignments().get(0).getPointsPossible());
        cache.saveSnapshot(new CalendarSnapshot(List.of(), List.of(), due.plusSeconds(1)));
        assertEquals(due.plusSeconds(1), cache.readSnapshot().orElseThrow().getSyncedAt());
        assertTrue(cache.readSnapshot().orElseThrow().getAssignments().isEmpty());
    }

    @Test
    void reportsIncompleteCacheAsReadFailure() throws IOException {
        Path file = Files.writeString(directory.resolve("calendar.json"), "{}");
        assertThrows(IOException.class, new JsonCalendarDataAccessObject(file)::readSnapshot);
    }
}
