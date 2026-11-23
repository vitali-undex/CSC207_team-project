package entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradebookTest {

    @Test
    void holdsACoursesGroups() {
        List<AssignmentGroup> groups = new ArrayList<>();
        groups.add(new AssignmentGroup("midterm", "Midterm", List.of(new GradedAssignment("Midterm", 34.0, 50))));
        Gradebook gradebook = new Gradebook("course-1", "Course One", groups);

        groups.clear();

        assertEquals("course-1", gradebook.getCourseId());
        assertEquals("Course One", gradebook.getCourseName());
        assertEquals(1, gradebook.getGroups().size());
        assertEquals("Midterm", gradebook.getGroups().get(0).getName());
        assertThrows(UnsupportedOperationException.class, () -> gradebook.getGroups().clear());
    }

    @Test
    void courseIdAndNameAreRequired() {
        assertThrows(NullPointerException.class, () -> new Gradebook(null, "Course One", List.of()));
        assertThrows(NullPointerException.class, () -> new Gradebook("course-1", null, List.of()));
    }
}
