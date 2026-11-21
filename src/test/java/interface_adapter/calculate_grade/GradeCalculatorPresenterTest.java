package interface_adapter.calculate_grade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.calculate_grade.AveragingOption;
import use_case.calculate_grade.CalculateGradeOutputData;
import use_case.calculate_grade.CourseOption;
import use_case.calculate_grade.GroupResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeCalculatorPresenterTest {

    private static final List<AveragingOption> OPTIONS =
            List.of(new AveragingOption("points", "By points"), new AveragingOption("equal", "Equally"));

    private GradeCalculatorViewModel viewModel;
    private GradeCalculatorPresenter presenter;
    private final List<String> events = new ArrayList<>();

    @BeforeEach
    void setUp() {
        viewModel = new GradeCalculatorViewModel();
        viewModel.addPropertyChangeListener(event -> {
            events.add(event.getPropertyName());
            assertSame(viewModel.getState(), event.getNewValue());
        });
        presenter = new GradeCalculatorPresenter(viewModel);
    }

    private static CalculateGradeOutputData breakdown(boolean schemeSaved, Double grade, double gradedWeight) {
        return new CalculateGradeOutputData("course-1", "Course One", List.of(
                new GroupResult("quizzes", "Quizzes", 20, 1, "equal", 2, 3, 200.0 / 3),
                new GroupResult("final", "Final exam", 33.333, 0, "points", 0, 1, null)),
                OPTIONS, schemeSaved, grade, gradedWeight);
    }

    @Test
    void coursesBecomeChoices() {
        viewModel.getState().setErrorMessage("old error");

        presenter.prepareCourseListView(
                List.of(new CourseOption("course-1", "Course One"), new CourseOption("course-2", "Course Two")));

        GradeCalculatorState state = viewModel.getState();
        assertEquals(List.of(new Choice("course-1", "Course One"), new Choice("course-2", "Course Two")),
                state.getCourses());
        assertEquals("Course Two", state.getCourses().get(1).getLabel());
        assertEquals("", state.getErrorMessage());
        assertEquals(List.of(GradeCalculatorViewModel.COURSES_PROPERTY), events);
    }

    @Test
    void courseWithoutASchemeAsksForWeights() {
        presenter.prepareCourseView(breakdown(false, null, 0));

        GradeCalculatorState state = viewModel.getState();
        assertEquals("course-1", state.getCourseId());
        assertEquals("Course One", state.getCourseName());
        assertEquals("", state.getGradeText());
        assertEquals(GradeCalculatorPresenter.ENTER_WEIGHTS_MESSAGE, state.getDetailText());
        assertEquals(List.of(new Choice("points", "By points"), new Choice("equal", "Equally")),
                state.getAveragingChoices());

        GroupRowState quizzes = state.getRows().get(0);
        assertEquals("quizzes", quizzes.getGroupId());
        assertEquals("Quizzes", quizzes.getName());
        assertEquals("20", quizzes.getWeightText());
        assertEquals("1", quizzes.getDropText());
        assertEquals("Equally", quizzes.getAveraging().getLabel());
        assertEquals("66.67%", quizzes.getAverageText());
        assertEquals("2 of 3", quizzes.getGradedText());

        GroupRowState finalExam = state.getRows().get(1);
        assertEquals("33.33", finalExam.getWeightText());
        assertEquals(GradeCalculatorPresenter.NO_AVERAGE, finalExam.getAverageText());
        assertEquals(List.of(GradeCalculatorViewModel.STATE_PROPERTY), events);
    }

    @Test
    void courseWithASavedSchemeShowsItsGrade() {
        presenter.prepareCourseView(breakdown(true, 72.8, 50));

        assertEquals("Current grade: 72.8%", viewModel.getState().getGradeText());
        assertEquals("Based on 50% of the course graded so far. Using the weights you saved.",
                viewModel.getState().getDetailText());
    }

    @Test
    void successShowsTheGradeRoundedToTwoPlaces() {
        presenter.prepareSuccessView(breakdown(true, 5760.0 / 70, 70));

        assertEquals("Current grade: 82.29%", viewModel.getState().getGradeText());
        assertEquals("Based on 70% of the course graded so far. Weights saved.", viewModel.getState().getDetailText());
        assertEquals("", viewModel.getState().getErrorMessage());
    }

    @Test
    void successWithNothingGradedSaysNoGradeYet() {
        presenter.prepareSuccessView(breakdown(true, null, 0));

        assertEquals("No grade yet", viewModel.getState().getGradeText());
        assertEquals("Nothing with a weight has been graded yet. Weights saved.", viewModel.getState().getDetailText());
    }

    @Test
    void failureKeepsTheRowsAndClearsTheGrade() {
        presenter.prepareSuccessView(breakdown(true, 72.8, 50));
        events.clear();

        presenter.prepareFailView("The weights add up to 95%.");

        GradeCalculatorState state = viewModel.getState();
        assertEquals(2, state.getRows().size());
        assertEquals("", state.getGradeText());
        assertEquals("", state.getDetailText());
        assertEquals("The weights add up to 95%.", state.getErrorMessage());
        assertEquals(List.of(GradeCalculatorViewModel.ERROR_PROPERTY), events);
    }

    @Test
    void loadFailureClearsTheCourse() {
        presenter.prepareSuccessView(breakdown(true, 72.8, 50));
        events.clear();

        presenter.prepareLoadFailView("Couldn't load your grades.");

        GradeCalculatorState state = viewModel.getState();
        assertTrue(state.getRows().isEmpty());
        assertEquals("", state.getCourseId());
        assertEquals("", state.getCourseName());
        assertEquals("", state.getGradeText());
        assertEquals("Couldn't load your grades.", state.getErrorMessage());
        assertEquals(List.of(GradeCalculatorViewModel.STATE_PROPERTY), events);
    }

    @Test
    void unknownAveragingRuleIsShownByItsId() {
        presenter.prepareCourseView(new CalculateGradeOutputData("course-1", "Course One",
                List.of(new GroupResult("labs", "Labs", 10, 0, "median", 1, 1, 50.0)), OPTIONS, false, null, 0));

        assertEquals("median", viewModel.getState().getRows().get(0).getAveraging().getLabel());
    }
}
