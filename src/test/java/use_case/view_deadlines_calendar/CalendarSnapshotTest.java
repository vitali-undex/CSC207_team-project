package use_case.view_deadlines_calendar;

import entity.Assignment;
import entity.Course;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarSnapshotTest {
    @Test
    void takesAnImmutableCopyOfTheSyncResult() {
        final Course course = new Course("1", "CSC207", "Design");
        final List<Course> courses = new ArrayList<>(List.of(course));
        final List<Assignment> assignments = new ArrayList<>();
        final Instant synced = Instant.parse("2028-01-01T12:00:00Z");
        final CalendarSnapshot snapshot = new CalendarSnapshot(courses, assignments, synced);

        courses.clear();
        assignments.add(new Assignment("2", "1", "Quiz", synced, null));
        assertEquals(List.of(course), snapshot.getCourses());
        assertTrue(snapshot.getAssignments().isEmpty());
        assertEquals(synced, snapshot.getSyncedAt());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getCourses().clear());
    }
}
