package interface_adapter.calculate_grade;

import org.junit.jupiter.api.Test;
import use_case.calculate_grade.CalculateGradeInputBoundary;
import use_case.calculate_grade.CalculateGradeInputData;
import use_case.calculate_grade.GroupWeightInput;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GradeCalculatorControllerTest {

    private final RecordingInteractor interactor = new RecordingInteractor();
    private final GradeCalculatorController controller = new GradeCalculatorController(interactor);

    @Test
    void loadingAndPickingCoursesGoToTheUseCase() {
        controller.loadCourses();
        controller.selectCourse("course-1");

        assertEquals(1, interactor.loadCoursesCalls);
        assertEquals("course-1", interactor.loadedCourseId);
    }

    @Test
    void calculateSendsWhatTheStudentTypedAsNumbers() {
        controller.calculate("course-1", List.of(
                new WeightEntry("quizzes", "20", "1", "equal"),
                new WeightEntry("midterm", " 30% ", "", "points"),
                new WeightEntry("final", "50.5", " 2 ", "points")));

        CalculateGradeInputData sent = interactor.executed;
        assertEquals("course-1", sent.getCourseId());
        GroupWeightInput quizzes = sent.getWeights().get(0);
        assertEquals("quizzes", quizzes.getGroupId());
        assertEquals(20.0, quizzes.getWeightPercent());
        assertEquals(1, quizzes.getDropLowest());
        assertEquals("equal", quizzes.getAveragingId());
        assertEquals(30.0, sent.getWeights().get(1).getWeightPercent());
        assertEquals(0, sent.getWeights().get(1).getDropLowest());
        assertEquals(50.5, sent.getWeights().get(2).getWeightPercent());
        assertEquals(2, sent.getWeights().get(2).getDropLowest());
    }

    @Test
    void weightsThatArentNumbersBecomeNull() {
        for (String text : new String[] {"", "  ", "%", "abc", "12,5", "NaN", "Infinity", null}) {
            assertNull(GradeCalculatorController.parseWeight(text), "weight " + text);
        }
        assertEquals(12.5, GradeCalculatorController.parseWeight("12.5 %"));
    }

    @Test
    void blankDropsMeanNoneAndOtherTextIsNotANumber() {
        assertEquals(0, GradeCalculatorController.parseDrop(""));
        assertEquals(0, GradeCalculatorController.parseDrop("   "));
        assertEquals(0, GradeCalculatorController.parseDrop(null));
        assertEquals(-1, GradeCalculatorController.parseDrop("-1"));
        assertNull(GradeCalculatorController.parseDrop("1.5"));
        assertNull(GradeCalculatorController.parseDrop("one"));
    }

    /** Remembers what the controller asked for. */
    private static final class RecordingInteractor implements CalculateGradeInputBoundary {
        private int loadCoursesCalls;
        private String loadedCourseId;
        private CalculateGradeInputData executed;

        @Override
        public void loadCourses() {
            loadCoursesCalls++;
        }

        @Override
        public void loadCourse(String courseId) {
            loadedCourseId = courseId;
        }

        @Override
        public void execute(CalculateGradeInputData inputData) {
            executed = inputData;
        }
    }
}
