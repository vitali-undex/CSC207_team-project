package data_access;

import entity.AssignmentGroup;
import entity.Gradebook;
import entity.GradingScheme;
import entity.GroupWeight;
import entity.PointsAveraging;
import org.junit.jupiter.api.Test;
import use_case.calculate_grade.CourseOption;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoGradeDataAccessObjectTest {

    private static final double DELTA = 1e-9;
    private final DemoGradeDataAccessObject demo = new DemoGradeDataAccessObject();

    @Test
    void offersThreeMadeUpCourses() {
        List<CourseOption> courses = demo.getCourses();

        assertEquals(List.of("demo-101", "demo-205", "demo-310"),
                List.of(courses.get(0).getId(), courses.get(1).getId(), courses.get(2).getId()));
        for (CourseOption course : courses) {
            assertTrue(course.getName().startsWith("DEMO"), course.getName());
        }
    }

    @Test
    void demo101GivesTheGradeShownInTheDemo() throws IOException {
        Gradebook gradebook = demo.getGradebook("demo-101");
        GradingScheme syllabus = new GradingScheme("demo-101", List.of(
                new GroupWeight("demo-101-quizzes", 20, 0, new PointsAveraging()),
                new GroupWeight("demo-101-assignments", 30, 0, new PointsAveraging()),
                new GroupWeight("demo-101-midterm", 20, 0, new PointsAveraging()),
                new GroupWeight("demo-101-final", 30, 0, new PointsAveraging())));

        assertEquals("DEMO101 · Introduction to Demo Studies", gradebook.getCourseName());
        assertEquals(4, gradebook.getGroups().size());
        assertEquals(4, gradebook.getGroups().get(0).getGradedCount());
        assertEquals(5693.0 / 70 + 1.0 / 210, syllabus.weightedGrade(gradebook).getAsDouble(), 1e-6);
        assertEquals(70.0, syllabus.gradedWeight(gradebook), DELTA);
    }

    @Test
    void demo310HasNothingGradedYet() throws IOException {
        for (AssignmentGroup group : demo.getGradebook("demo-310").getGroups()) {
            assertEquals(0, group.getGradedCount(), group.getName());
        }
    }

    @Test
    void unknownCoursesCantBeLoaded() {
        assertThrows(IOException.class, () -> demo.getGradebook("12345"));
    }
}
