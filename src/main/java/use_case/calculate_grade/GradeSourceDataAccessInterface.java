package use_case.calculate_grade;

import entity.Gradebook;

import java.io.IOException;
import java.util.List;

/**
 * Where the grade calculator reads courses and scores from. For now a demo
 * data source implements this; a Quercus data access object will implement
 * it once the connect Quercus use case is in place.
 */
public interface GradeSourceDataAccessInterface {

    /**
     * Returns the courses the student can pick.
     *
     * @return the courses, in the order to show them
     * @throws IOException if the courses can't be read
     */
    List<CourseOption> getCourses() throws IOException;

    /**
     * Returns one course's assignment groups and the student's scores.
     *
     * @param courseId the course's id
     * @return the course's gradebook
     * @throws IOException if the course can't be read or doesn't exist
     */
    Gradebook getGradebook(String courseId) throws IOException;
}
