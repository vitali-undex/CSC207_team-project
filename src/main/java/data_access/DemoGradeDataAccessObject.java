package data_access;

import entity.AssignmentGroup;
import entity.GradedAssignment;
import entity.Gradebook;
import use_case.calculate_grade.CourseOption;
import use_case.calculate_grade.GradeSourceDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Made-up courses and marks for demos and manual testing. None of this is real
 * student data: the courses, groups and marks are invented. A Quercus data
 * access object will implement the same interface once the connect Quercus use
 * case is in place, and nothing else in the grade calculator changes.
 */
public final class DemoGradeDataAccessObject implements GradeSourceDataAccessInterface {

    private final Map<String, Gradebook> gradebooks = new LinkedHashMap<>();

    /**
     * Creates the demo data: three made-up courses.
     */
    public DemoGradeDataAccessObject() {
        add(new Gradebook("demo-101", "DEMO101 · Introduction to Demo Studies", List.of(
                group("demo-101-quizzes", "Quizzes",
                        graded("Quiz 1", 8, 10), graded("Quiz 2", 3, 5), graded("Quiz 3", 18, 20),
                        graded("Quiz 4", 10, 10), notGraded("Quiz 5", 10)),
                group("demo-101-assignments", "Assignments",
                        graded("Assignment 1", 18, 20), graded("Assignment 2", 21, 25),
                        notGraded("Assignment 3", 25)),
                group("demo-101-midterm", "Midterm", graded("Midterm", 34, 50)),
                group("demo-101-final", "Final exam", notGraded("Final exam", 100)))));
        add(new Gradebook("demo-205", "DEMO205 · Sample Statistics", List.of(
                group("demo-205-problem-sets", "Problem sets",
                        graded("Problem set 1", 9, 10), graded("Problem set 2", 10, 10),
                        graded("Problem set 3", 7, 10), graded("Problem set 4", 8, 10),
                        graded("Problem set 5", 9, 10), notGraded("Problem set 6", 10)),
                group("demo-205-labs", "Labs",
                        graded("Lab 1", 2, 2), graded("Lab 2", 2, 2), graded("Lab 3", 1, 2),
                        graded("Lab 4", 2, 2), graded("Lab 5", 2, 2), notGraded("Lab 6", 2)),
                group("demo-205-test-1", "Term test 1", graded("Term test 1", 41, 50)),
                group("demo-205-test-2", "Term test 2", graded("Term test 2", 38, 50)),
                group("demo-205-final", "Final exam", notGraded("Final exam", 100)))));
        add(new Gradebook("demo-310", "DEMO310 · Pretend Economics", List.of(
                group("demo-310-problem-sets", "Problem sets",
                        notGraded("Problem set 1", 20), notGraded("Problem set 2", 20),
                        notGraded("Problem set 3", 20)),
                group("demo-310-midterm", "Midterm", notGraded("Midterm", 60)),
                group("demo-310-final", "Final exam", notGraded("Final exam", 100)))));
    }

    @Override
    public List<CourseOption> getCourses() {
        final List<CourseOption> courses = new ArrayList<>();
        for (Gradebook gradebook : gradebooks.values()) {
            courses.add(new CourseOption(gradebook.getCourseId(), gradebook.getCourseName()));
        }
        return courses;
    }

    @Override
    public Gradebook getGradebook(String courseId) throws IOException {
        final Gradebook gradebook = gradebooks.get(courseId);
        if (gradebook == null) {
            throw new IOException("There is no demo course with the id " + courseId);
        }
        return gradebook;
    }

    private void add(Gradebook gradebook) {
        gradebooks.put(gradebook.getCourseId(), gradebook);
    }

    private static AssignmentGroup group(String id, String name, GradedAssignment... assignments) {
        return new AssignmentGroup(id, name, List.of(assignments));
    }

    private static GradedAssignment graded(String name, double earned, double possible) {
        return new GradedAssignment(name, earned, possible);
    }

    private static GradedAssignment notGraded(String name, double possible) {
        return new GradedAssignment(name, null, possible);
    }
}
