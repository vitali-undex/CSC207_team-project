package use_case.calculate_grade;

import entity.AssignmentGroup;
import entity.EqualWeightAveraging;
import entity.GradedAssignment;
import entity.Gradebook;
import entity.GradingScheme;
import entity.GroupWeight;
import entity.PointsAveraging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculateGradeInteractorTest {

    private static final double DELTA = 1e-9;
    /** Quizzes by points: (8 + 3) / (10 + 5). */
    private static final double QUIZZES_BY_POINTS = 11.0 / 15 * 100;

    private FakeGradeSource source;
    private FakeSchemeStore store;
    private RecordingPresenter presenter;
    private CalculateGradeInteractor interactor;

    @BeforeEach
    void setUp() {
        source = new FakeGradeSource();
        source.gradebooks.put("course-1", new Gradebook("course-1", "Course One", List.of(
                new AssignmentGroup("quizzes", "Quizzes", List.of(
                        new GradedAssignment("Quiz 1", 8.0, 10),
                        new GradedAssignment("Quiz 2", 3.0, 5),
                        new GradedAssignment("Quiz 3", null, 10))),
                new AssignmentGroup("midterm", "Midterm", List.of(new GradedAssignment("Midterm", 34.0, 50))),
                new AssignmentGroup("final", "Final exam",
                        List.of(new GradedAssignment("Final exam", null, 100))))));
        source.gradebooks.put("course-2", new Gradebook("course-2", "Course Two", List.of(
                new AssignmentGroup("work", "Work", List.of(new GradedAssignment("Essay", null, 10))))));
        store = new FakeSchemeStore();
        presenter = new RecordingPresenter();
        interactor = new CalculateGradeInteractor(source, store, presenter);
    }

    private static GroupWeightInput input(String groupId, Double weight, Integer drop, String averaging) {
        return new GroupWeightInput(groupId, weight, drop, averaging);
    }

    private void calculate(String courseId, GroupWeightInput... inputs) {
        interactor.execute(new CalculateGradeInputData(courseId, List.of(inputs)));
    }

    private void calculateCourseOne(double quizzes, double midterm, double finalExam) {
        calculate("course-1", input("quizzes", quizzes, 0, "points"), input("midterm", midterm, 0, "points"),
                input("final", finalExam, 0, "points"));
    }

    @Test
    void loadCoursesShowsEveryCourse() {
        source.courses = List.of(new CourseOption("course-1", "Course One"),
                new CourseOption("course-2", "Course Two"));

        interactor.loadCourses();

        assertEquals(2, presenter.courses.size());
        assertEquals("course-1", presenter.courses.get(0).getId());
        assertEquals("Course Two", presenter.courses.get(1).getName());
        assertNull(presenter.loadFailMessage);
    }

    @Test
    void loadCoursesSaysSoWhenThereAreNone() {
        interactor.loadCourses();

        assertEquals(CalculateGradeInteractor.NO_COURSES_MESSAGE, presenter.loadFailMessage);
        assertNull(presenter.courses);
    }

    @Test
    void loadCoursesSaysSoWhenTheyCantBeRead() {
        source.failing = true;

        interactor.loadCourses();

        assertEquals(CalculateGradeInteractor.LOAD_FAILED_MESSAGE, presenter.loadFailMessage);
    }

    @Test
    void courseWithoutASavedSchemeShowsItsGroupsWithNoWeights() {
        interactor.loadCourse("course-1");

        CalculateGradeOutputData shown = presenter.courseView;
        assertEquals("course-1", shown.getCourseId());
        assertEquals("Course One", shown.getCourseName());
        assertFalse(shown.isSchemeSaved());
        assertNull(shown.getWeightedGrade());
        assertEquals(0.0, shown.getGradedWeight(), DELTA);

        GroupResult quizzes = shown.getGroups().get(0);
        assertEquals("quizzes", quizzes.getGroupId());
        assertEquals("Quizzes", quizzes.getName());
        assertEquals(0.0, quizzes.getWeightPercent(), DELTA);
        assertEquals(0, quizzes.getDropLowest());
        assertEquals(PointsAveraging.ID, quizzes.getAveragingId());
        assertEquals(2, quizzes.getGradedCount());
        assertEquals(3, quizzes.getAssignmentCount());
        assertEquals(QUIZZES_BY_POINTS, quizzes.getAveragePercent(), DELTA);
        assertNull(shown.getGroups().get(2).getAveragePercent());

        assertEquals(2, shown.getAveragingOptions().size());
        assertEquals("points", shown.getAveragingOptions().get(0).getId());
        assertEquals("By points", shown.getAveragingOptions().get(0).getName());
        assertEquals("Equally", shown.getAveragingOptions().get(1).getName());
    }

    @Test
    void courseWithASavedSchemeFillsInTheWeightsAndShowsTheGrade() {
        store.schemes.put("course-1", new GradingScheme("course-1", List.of(
                new GroupWeight("quizzes", 20, 1, new EqualWeightAveraging()),
                new GroupWeight("midterm", 30, 0, new PointsAveraging()),
                new GroupWeight("final", 50, 0, new PointsAveraging()))));

        interactor.loadCourse("course-1");

        CalculateGradeOutputData shown = presenter.courseView;
        assertTrue(shown.isSchemeSaved());
        assertEquals((80.0 * 20 + 68.0 * 30) / 50, shown.getWeightedGrade(), DELTA);
        assertEquals(50.0, shown.getGradedWeight(), DELTA);
        GroupResult quizzes = shown.getGroups().get(0);
        assertEquals(20.0, quizzes.getWeightPercent(), DELTA);
        assertEquals(1, quizzes.getDropLowest());
        assertEquals(EqualWeightAveraging.ID, quizzes.getAveragingId());
        assertEquals(80.0, quizzes.getAveragePercent(), DELTA);
    }

    @Test
    void courseThatCantBeLoadedSaysSo() {
        interactor.loadCourse("no-such-course");

        assertEquals(CalculateGradeInteractor.LOAD_FAILED_MESSAGE, presenter.loadFailMessage);
        assertNull(presenter.courseView);
    }

    @Test
    void courseSaysSoWhenSavedSchemesCantBeRead() {
        store.failingLoad = true;

        interactor.loadCourse("course-1");

        assertEquals(CalculateGradeInteractor.LOAD_FAILED_MESSAGE, presenter.loadFailMessage);
    }

    @Test
    void calculatingSavesTheSchemeAndShowsTheGrade() {
        calculateCourseOne(20, 30, 50);

        CalculateGradeOutputData shown = presenter.success;
        assertTrue(shown.isSchemeSaved());
        assertEquals("Course One", shown.getCourseName());
        assertEquals((QUIZZES_BY_POINTS * 20 + 68.0 * 30) / 50, shown.getWeightedGrade(), DELTA);
        assertEquals(50.0, shown.getGradedWeight(), DELTA);
        assertEquals(30.0, shown.getGroups().get(1).getWeightPercent(), DELTA);

        GradingScheme saved = store.schemes.get("course-1");
        assertEquals(3, saved.getWeights().size());
        assertEquals("final", saved.getWeights().get(2).getGroupId());
        assertEquals(50.0, saved.getWeights().get(2).getWeightPercent(), DELTA);
        assertNull(presenter.failMessage);
    }

    @Test
    void calculatingUsesEachGroupsDropsAndAveraging() {
        calculate("course-1", input("quizzes", 20.0, 1, "equal"), input("midterm", 30.0, 0, "points"),
                input("final", 50.0, 0, "points"));

        assertEquals((80.0 * 20 + 68.0 * 30) / 50, presenter.success.getWeightedGrade(), DELTA);
        assertTrue(store.schemes.get("course-1").getWeights().get(0).getAveraging() instanceof EqualWeightAveraging);
    }

    @Test
    void nothingGradedMeansNoGradeYet() {
        calculate("course-2", input("work", 100.0, 0, "points"));

        assertNull(presenter.success.getWeightedGrade());
        assertEquals(0.0, presenter.success.getGradedWeight(), DELTA);
        assertTrue(store.schemes.containsKey("course-2"));
    }

    @Test
    void weightsUnderOneHundredAreRefusedWithTheDifference() {
        calculateCourseOne(20, 30, 45);

        assertEquals("The weights add up to 95%. They need to add up to 100% (5% missing).", presenter.failMessage);
        assertTrue(store.schemes.isEmpty());
        assertNull(presenter.success);
    }

    @Test
    void weightsOverOneHundredAreRefusedWithTheDifference() {
        calculateCourseOne(20, 30, 60);

        assertEquals("The weights add up to 110%. They need to add up to 100% (10% too much).",
                presenter.failMessage);
        assertTrue(store.schemes.isEmpty());
    }

    @Test
    void numbersThatArentNumbersAreRefused() {
        calculate("course-1", input("quizzes", null, 0, "points"));
        assertEquals(CalculateGradeInteractor.MISSING_NUMBER_MESSAGE, presenter.failMessage);

        presenter.failMessage = null;
        calculate("course-1", input("quizzes", 100.0, null, "points"));
        assertEquals(CalculateGradeInteractor.MISSING_NUMBER_MESSAGE, presenter.failMessage);
        assertTrue(store.schemes.isEmpty());
    }

    @Test
    void weightsOutsideZeroToOneHundredAreRefused() {
        for (double weight : new double[] {-5, 120, Double.NaN}) {
            presenter.failMessage = null;
            calculate("course-1", input("quizzes", weight, 0, "points"));
            assertEquals(CalculateGradeInteractor.WEIGHT_RANGE_MESSAGE, presenter.failMessage);
        }
        assertTrue(store.schemes.isEmpty());
    }

    @Test
    void negativeDropsAreRefused() {
        calculate("course-1", input("quizzes", 100.0, -1, "points"));

        assertEquals(CalculateGradeInteractor.DROP_RANGE_MESSAGE, presenter.failMessage);
    }

    @Test
    void unknownAveragingRulesAreRefused() {
        calculate("course-1", input("quizzes", 100.0, 0, "median"));
        assertEquals(CalculateGradeInteractor.AVERAGING_MESSAGE, presenter.failMessage);

        presenter.failMessage = null;
        calculate("course-1", input("quizzes", 100.0, 0, null));
        assertEquals(CalculateGradeInteractor.AVERAGING_MESSAGE, presenter.failMessage);
    }

    @Test
    void gradesThatCantBeLoadedKeepWhatTheStudentTyped() {
        source.failing = true;

        calculateCourseOne(20, 30, 50);

        assertEquals(CalculateGradeInteractor.LOAD_FAILED_MESSAGE, presenter.failMessage);
        assertNull(presenter.loadFailMessage);
        assertTrue(store.schemes.isEmpty());
    }

    @Test
    void aSchemeThatCantBeSavedShowsNoGrade() {
        store.failingSave = true;

        calculateCourseOne(20, 30, 50);

        assertEquals(CalculateGradeInteractor.SAVE_FAILED_MESSAGE, presenter.failMessage);
        assertNull(presenter.success);
    }

    @Test
    void weightTotalMessageRoundsToTwoDecimalPlaces() {
        assertEquals("The weights add up to 99.5%. They need to add up to 100% (0.5% missing).",
                CalculateGradeInteractor.weightTotalMessage(99.5));
        assertEquals("The weights add up to 33.33%. They need to add up to 100% (66.67% missing).",
                CalculateGradeInteractor.weightTotalMessage(100.0 / 3));
    }

    @Test
    void inputDataKeepsItsOwnCopyOfTheWeights() {
        List<GroupWeightInput> weights = new ArrayList<>();
        weights.add(input("quizzes", 20.0, 0, "points"));
        CalculateGradeInputData inputData = new CalculateGradeInputData("course-1", weights);

        weights.clear();

        assertEquals("course-1", inputData.getCourseId());
        assertEquals(1, inputData.getWeights().size());
    }

    /** A grade source that serves gradebooks from memory, or fails when told to. */
    private static final class FakeGradeSource implements GradeSourceDataAccessInterface {
        private List<CourseOption> courses = List.of();
        private final Map<String, Gradebook> gradebooks = new HashMap<>();
        private boolean failing;

        @Override
        public List<CourseOption> getCourses() throws IOException {
            if (failing) {
                throw new IOException("offline");
            }
            return courses;
        }

        @Override
        public Gradebook getGradebook(String courseId) throws IOException {
            if (failing || !gradebooks.containsKey(courseId)) {
                throw new IOException("can't load " + courseId);
            }
            return gradebooks.get(courseId);
        }
    }

    /** A scheme store that keeps schemes in memory, or fails when told to. */
    private static final class FakeSchemeStore implements GradingSchemeDataAccessInterface {
        private final Map<String, GradingScheme> schemes = new HashMap<>();
        private boolean failingLoad;
        private boolean failingSave;

        @Override
        public Optional<GradingScheme> loadScheme(String courseId) throws IOException {
            if (failingLoad) {
                throw new IOException("can't read");
            }
            return Optional.ofNullable(schemes.get(courseId));
        }

        @Override
        public void saveScheme(GradingScheme scheme) throws IOException {
            if (failingSave) {
                throw new IOException("disk full");
            }
            schemes.put(scheme.getCourseId(), scheme);
        }
    }

    /** Remembers the last thing the interactor asked it to show. */
    private static final class RecordingPresenter implements CalculateGradeOutputBoundary {
        private List<CourseOption> courses;
        private CalculateGradeOutputData courseView;
        private CalculateGradeOutputData success;
        private String failMessage;
        private String loadFailMessage;

        @Override
        public void prepareCourseListView(List<CourseOption> courseOptions) {
            courses = courseOptions;
        }

        @Override
        public void prepareCourseView(CalculateGradeOutputData outputData) {
            courseView = outputData;
        }

        @Override
        public void prepareSuccessView(CalculateGradeOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failMessage = errorMessage;
        }

        @Override
        public void prepareLoadFailView(String errorMessage) {
            loadFailMessage = errorMessage;
        }
    }
}
