package interface_adapter.calculate_grade;

import use_case.calculate_grade.CalculateGradeInputBoundary;
import use_case.calculate_grade.CalculateGradeInputData;
import use_case.calculate_grade.GroupWeightInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns what the student does on the grade calculator screen into requests for
 * the calculate grade use case.
 */
public class GradeCalculatorController {

    private final CalculateGradeInputBoundary interactor;

    /**
     * Creates the controller.
     *
     * @param interactor the use case to call
     */
    public GradeCalculatorController(CalculateGradeInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Loads the courses the student can pick.
     */
    public void loadCourses() {
        interactor.loadCourses();
    }

    /**
     * Shows the course the student picked.
     *
     * @param courseId the course's id
     */
    public void selectCourse(String courseId) {
        interactor.loadCourse(courseId);
    }

    /**
     * Calculates the grade from what the student typed in the table.
     *
     * @param courseId the course on screen
     * @param entries  one entry per row of the table
     */
    public void calculate(String courseId, List<WeightEntry> entries) {
        final List<GroupWeightInput> weights = new ArrayList<>();
        for (WeightEntry entry : entries) {
            weights.add(new GroupWeightInput(entry.getGroupId(), parseWeight(entry.getWeightText()),
                    entry.getAveragingId(), parseCount(entry.getDropText()),
                    parseCount(entry.getKeepBestText()), entry.isCapAtFullMarks()));
        }
        interactor.execute(new CalculateGradeInputData(courseId, weights));
    }

    /**
     * Reads a weight such as "20", "12.5" or "20%".
     *
     * @param text what the student typed
     * @return the weight, or null if it isn't a number
     */
    static Double parseWeight(String text) {
        String cleaned = "";
        if (text != null) {
            cleaned = text.strip();
        }
        if (cleaned.endsWith("%")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).strip();
        }
        Double weight = null;
        try {
            final double value = Double.parseDouble(cleaned);
            if (Double.isFinite(value)) {
                weight = value;
            }
        }
        catch (NumberFormatException exception) {
            weight = null;
        }
        return weight;
    }

    /**
     * Reads a drop-lowest or count-best number. Blank means 0: drop none, or
     * count all.
     *
     * @param text what the student typed
     * @return the number, or null if it isn't a whole number
     */
    static Integer parseCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        Integer drop;
        try {
            drop = Integer.valueOf(text.strip());
        }
        catch (NumberFormatException exception) {
            drop = null;
        }
        return drop;
    }
}
