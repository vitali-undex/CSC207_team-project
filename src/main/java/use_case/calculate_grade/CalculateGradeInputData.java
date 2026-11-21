package use_case.calculate_grade;

import java.util.List;

/**
 * The grading scheme the student entered for one course.
 */
public class CalculateGradeInputData {

    private final String courseId;
    private final List<GroupWeightInput> weights;

    /**
     * Creates the input data.
     *
     * @param courseId the course's id
     * @param weights  the weight and drop count entered for each assignment group
     */
    public CalculateGradeInputData(String courseId, List<GroupWeightInput> weights) {
        this.courseId = courseId;
        this.weights = List.copyOf(weights);
    }

    public String getCourseId() {
        return courseId;
    }

    public List<GroupWeightInput> getWeights() {
        return weights;
    }
}
