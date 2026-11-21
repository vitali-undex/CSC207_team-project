package use_case.calculate_grade;

/**
 * The input boundary for the calculate grade use case. The grade calculator
 * controller calls this; the interactor implements it.
 */
public interface CalculateGradeInputBoundary {

    /**
     * Shows the courses the student can pick.
     */
    void loadCourses();

    /**
     * Shows a course's assignment groups with any weights saved for it
     * (use case 3, steps 1 and 2).
     *
     * @param courseId the course the student picked
     */
    void loadCourse(String courseId);

    /**
     * Checks the scheme the student entered, saves it, and shows the course's
     * current grade (use case 3, steps 3 to 5).
     *
     * @param inputData the course and the weights the student entered
     */
    void execute(CalculateGradeInputData inputData);
}
