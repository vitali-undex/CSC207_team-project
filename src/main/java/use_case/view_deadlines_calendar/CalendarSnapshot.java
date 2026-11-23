package use_case.view_deadlines_calendar;

import entity.Assignment;
import entity.Course;

import java.time.Instant;
import java.util.List;

/** The complete set of courses and assignments from one successful load. */
public final class CalendarSnapshot {
    private final List<Course> courses;
    private final List<Assignment> assignments;
    private final Instant syncedAt;

    /** Captures copies of the courses and assignments and the time they were loaded. */
    public CalendarSnapshot(List<Course> courses, List<Assignment> assignments, Instant syncedAt) {
        this.courses = List.copyOf(courses);
        this.assignments = List.copyOf(assignments);
        this.syncedAt = syncedAt;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public Instant getSyncedAt() {
        return syncedAt;
    }
}
