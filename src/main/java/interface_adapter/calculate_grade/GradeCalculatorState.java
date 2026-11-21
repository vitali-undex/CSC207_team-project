package interface_adapter.calculate_grade;

import java.util.List;

/**
 * Everything the grade calculator screen shows.
 */
public class GradeCalculatorState {

    private List<Choice> courses = List.of();
    private List<Choice> averagingChoices = List.of();
    private String courseId = "";
    private String courseName = "";
    private List<GroupRowState> rows = List.of();
    private String gradeText = "";
    private String detailText = "";
    private String errorMessage = "";

    public List<Choice> getCourses() {
        return courses;
    }

    public void setCourses(List<Choice> courses) {
        this.courses = List.copyOf(courses);
    }

    public List<Choice> getAveragingChoices() {
        return averagingChoices;
    }

    public void setAveragingChoices(List<Choice> averagingChoices) {
        this.averagingChoices = List.copyOf(averagingChoices);
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<GroupRowState> getRows() {
        return rows;
    }

    public void setRows(List<GroupRowState> rows) {
        this.rows = List.copyOf(rows);
    }

    public String getGradeText() {
        return gradeText;
    }

    public void setGradeText(String gradeText) {
        this.gradeText = gradeText;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
