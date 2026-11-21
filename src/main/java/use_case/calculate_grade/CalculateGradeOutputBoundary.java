package use_case.calculate_grade;

import java.util.List;

/**
 * The output boundary for the calculate grade use case. The grade calculator
 * presenter implements this.
 */
public interface CalculateGradeOutputBoundary {

    /**
     * Shows the courses the student can pick.
     *
     * @param courses the courses, in the order to show them
     */
    void prepareCourseListView(List<CourseOption> courses);

    /**
     * Shows the course the student picked: its groups, the weights saved for
     * it, and its grade if a scheme is saved (use case 3, steps 1 and 2).
     *
     * @param outputData the course's breakdown
     */
    void prepareCourseView(CalculateGradeOutputData outputData);

    /**
     * Shows the grade calculated from the scheme the student just entered,
     * which has been saved (use case 3, step 5).
     *
     * @param outputData the course's breakdown and grade
     */
    void prepareSuccessView(CalculateGradeOutputData outputData);

    /**
     * Shows why the scheme the student entered wasn't used. Nothing was saved,
     * and what the student typed stays on screen to fix.
     *
     * @param errorMessage the message to show the student
     */
    void prepareFailView(String errorMessage);

    /**
     * Shows that courses or grades couldn't be loaded, so there is nothing to
     * calculate.
     *
     * @param errorMessage the message to show the student
     */
    void prepareLoadFailView(String errorMessage);
}
