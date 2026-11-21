package use_case.calculate_grade;

import entity.GradingScheme;

import java.io.IOException;
import java.util.Optional;

/**
 * Where the grade calculator saves each course's grading scheme, so the
 * student only types it in once.
 */
public interface GradingSchemeDataAccessInterface {

    /**
     * Returns the grading scheme saved for a course.
     *
     * @param courseId the course's id
     * @return the saved scheme, or empty if none was saved yet
     * @throws IOException if saved schemes can't be read
     */
    Optional<GradingScheme> loadScheme(String courseId) throws IOException;

    /**
     * Saves a course's grading scheme, replacing any saved before.
     *
     * @param scheme the scheme to save
     * @throws IOException if the scheme can't be saved
     */
    void saveScheme(GradingScheme scheme) throws IOException;
}
