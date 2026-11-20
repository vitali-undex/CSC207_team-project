package entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseTest {
    @Test
    void exposesCourseIdentityAndLabels() {
        Course course = new Course("12", "CSC207", "Software Design");
        assertEquals("12", course.getId());
        assertEquals("CSC207", course.getCode());
        assertEquals("Software Design", course.getName());
    }
}
