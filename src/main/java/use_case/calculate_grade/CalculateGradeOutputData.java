package use_case.calculate_grade;

import java.util.List;

/**
 * A course's grade breakdown: one row per assignment group, and the current
 * grade if the course has a grading scheme.
 */
public class CalculateGradeOutputData {

    private final String courseId;
    private final String courseName;
    private final List<GroupResult> groups;
    private final List<AveragingOption> averagingOptions;
    private final boolean schemeSaved;
    private final Double weightedGrade;
    private final double gradedWeight;

    /**
     * Creates the output data.
     *
     * @param courseId         the course's id
     * @param courseName       the course's name
     * @param groups           one row per assignment group
     * @param averagingOptions the averaging rules the student can pick from
     * @param schemeSaved      true if the course has a saved grading scheme, so the grade is real
     * @param weightedGrade    the current grade as a percentage, or null if nothing weighted is
     *                         graded
     * @param gradedWeight     the total weight of the groups graded so far
     */
    public CalculateGradeOutputData(String courseId, String courseName, List<GroupResult> groups,
                                    List<AveragingOption> averagingOptions, boolean schemeSaved,
                                    Double weightedGrade, double gradedWeight) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.groups = List.copyOf(groups);
        this.averagingOptions = List.copyOf(averagingOptions);
        this.schemeSaved = schemeSaved;
        this.weightedGrade = weightedGrade;
        this.gradedWeight = gradedWeight;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<GroupResult> getGroups() {
        return groups;
    }

    public List<AveragingOption> getAveragingOptions() {
        return averagingOptions;
    }

    /**
     * Returns whether the course has a saved grading scheme.
     *
     * @return true if a scheme is saved, so the grade is based on it
     */
    public boolean isSchemeSaved() {
        return schemeSaved;
    }

    /**
     * Returns the current grade.
     *
     * @return the grade as a percentage, or null if nothing weighted is graded yet
     */
    public Double getWeightedGrade() {
        return weightedGrade;
    }

    public double getGradedWeight() {
        return gradedWeight;
    }
}
