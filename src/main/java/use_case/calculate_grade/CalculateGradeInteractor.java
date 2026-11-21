package use_case.calculate_grade;

import entity.AssignmentGroup;
import entity.AveragingStrategies;
import entity.AveragingStrategy;
import entity.Gradebook;
import entity.GradingScheme;
import entity.GroupWeight;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The calculate grade use case (use case 3).
 *
 * <ul>
 *   <li>Picking a course shows its assignment groups. If a grading scheme was
 *       saved for it, the weights are filled in and the grade is shown
 *       straight away (steps 1 and 2).</li>
 *   <li>A weight that isn't a number from 0 to 100, a drop count that isn't a
 *       whole number of 0 or more, or an unknown averaging rule is refused,
 *       and nothing is saved.</li>
 *   <li>Weights that don't add up to 100% are refused with the difference
 *       (extension 3a), and nothing is saved.</li>
 *   <li>Otherwise the scheme is saved and the current grade is shown, or
 *       "no grade yet" if nothing weighted is graded (extension 4a).</li>
 *   <li>If courses or grades can't be loaded, the student is told so
 *       (extension 2a).</li>
 * </ul>
 */
public class CalculateGradeInteractor implements CalculateGradeInputBoundary {

    /** Shown when the grade source has no courses. */
    public static final String NO_COURSES_MESSAGE = "There are no courses to show yet.";

    /** Shown when courses or grades can't be read. */
    public static final String LOAD_FAILED_MESSAGE = "Couldn't load your grades. Please try again.";

    /** Shown when the scheme can't be saved. */
    public static final String SAVE_FAILED_MESSAGE =
            "Couldn't save the grading scheme, so the grade wasn't updated. Please try again.";

    /** Shown when a weight or drop count isn't a number. */
    public static final String MISSING_NUMBER_MESSAGE =
            "Enter a number for every weight, and a whole number for every drop count.";

    /** Shown when a weight is below 0 or above 100. */
    public static final String WEIGHT_RANGE_MESSAGE = "Each weight must be between 0 and 100.";

    /** Shown when a drop count is negative. */
    public static final String DROP_RANGE_MESSAGE = "Drop counts can't be negative.";

    /** Shown when a group has no known averaging rule. */
    public static final String AVERAGING_MESSAGE =
            "Choose how the marks in each group are averaged.";

    private static final double MAX_WEIGHT = 100.0;

    private final GradeSourceDataAccessInterface gradeSource;
    private final GradingSchemeDataAccessInterface schemeStore;
    private final CalculateGradeOutputBoundary presenter;

    /**
     * Creates the interactor.
     *
     * @param gradeSource where courses and grades are read from
     * @param schemeStore where grading schemes are saved
     * @param presenter   receives the results
     */
    public CalculateGradeInteractor(GradeSourceDataAccessInterface gradeSource,
                                    GradingSchemeDataAccessInterface schemeStore,
                                    CalculateGradeOutputBoundary presenter) {
        this.gradeSource = gradeSource;
        this.schemeStore = schemeStore;
        this.presenter = presenter;
    }

    @Override
    public void loadCourses() {
        final List<CourseOption> courses;
        try {
            courses = gradeSource.getCourses();
        }
        catch (IOException exception) {
            presenter.prepareLoadFailView(LOAD_FAILED_MESSAGE);
            return;
        }
        if (courses.isEmpty()) {
            presenter.prepareLoadFailView(NO_COURSES_MESSAGE);
        }
        else {
            presenter.prepareCourseListView(courses);
        }
    }

    @Override
    public void loadCourse(String courseId) {
        final Gradebook gradebook;
        final Optional<GradingScheme> saved;
        try {
            gradebook = gradeSource.getGradebook(courseId);
            saved = schemeStore.loadScheme(courseId);
        }
        catch (IOException exception) {
            presenter.prepareLoadFailView(LOAD_FAILED_MESSAGE);
            return;
        }
        final GradingScheme scheme = saved.orElseGet(() -> new GradingScheme(courseId, List.of()));
        presenter.prepareCourseView(breakdown(gradebook, scheme, saved.isPresent()));
    }

    @Override
    public void execute(CalculateGradeInputData inputData) {
        final List<GroupWeight> weights = new ArrayList<>();
        for (GroupWeightInput input : inputData.getWeights()) {
            final Optional<String> problem = problemWith(input);
            if (problem.isPresent()) {
                presenter.prepareFailView(problem.get());
                return;
            }
            weights.add(new GroupWeight(input.getGroupId(), input.getWeightPercent(),
                    input.getDropLowest(), AveragingStrategies.byId(input.getAveragingId()).get()));
        }

        final GradingScheme scheme = new GradingScheme(inputData.getCourseId(), weights);
        if (!scheme.isComplete()) {
            presenter.prepareFailView(weightTotalMessage(scheme.getTotalWeight()));
            return;
        }

        final Gradebook gradebook;
        try {
            gradebook = gradeSource.getGradebook(inputData.getCourseId());
        }
        catch (IOException exception) {
            presenter.prepareFailView(LOAD_FAILED_MESSAGE);
            return;
        }
        try {
            schemeStore.saveScheme(scheme);
        }
        catch (IOException exception) {
            presenter.prepareFailView(SAVE_FAILED_MESSAGE);
            return;
        }
        presenter.prepareSuccessView(breakdown(gradebook, scheme, true));
    }

    /**
     * Returns what is wrong with one group's input, if anything.
     */
    private static Optional<String> problemWith(GroupWeightInput input) {
        final Double weight = input.getWeightPercent();
        final Optional<String> problem;
        if (weight == null || input.getDropLowest() == null) {
            problem = Optional.of(MISSING_NUMBER_MESSAGE);
        }
        else if (!(weight >= 0 && weight <= MAX_WEIGHT)) {
            problem = Optional.of(WEIGHT_RANGE_MESSAGE);
        }
        else if (input.getDropLowest() < 0) {
            problem = Optional.of(DROP_RANGE_MESSAGE);
        }
        else if (AveragingStrategies.byId(input.getAveragingId()).isEmpty()) {
            problem = Optional.of(AVERAGING_MESSAGE);
        }
        else {
            problem = Optional.empty();
        }
        return problem;
    }

    /**
     * Builds the course's breakdown under a scheme: one row per group, the
     * grade, and how much of the course is graded.
     */
    private static CalculateGradeOutputData breakdown(Gradebook gradebook, GradingScheme scheme,
                                                      boolean schemeSaved) {
        final List<GroupResult> rows = new ArrayList<>();
        for (AssignmentGroup group : gradebook.getGroups()) {
            final Optional<GroupWeight> weight = scheme.weightFor(group.getId());
            final double weightPercent = weight.map(GroupWeight::getWeightPercent).orElse(0.0);
            final int dropLowest = weight.map(GroupWeight::getDropLowest).orElse(0);
            final AveragingStrategy averaging = weight.map(GroupWeight::getAveraging)
                    .orElse(AveragingStrategies.defaultStrategy());
            final OptionalDouble average = group.average(dropLowest, averaging);
            rows.add(new GroupResult(group.getId(), group.getName(), weightPercent, dropLowest,
                    averaging.getId(), group.getGradedCount(), group.getAssignments().size(),
                    average.isPresent() ? average.getAsDouble() : null));
        }
        final List<AveragingOption> options = new ArrayList<>();
        for (AveragingStrategy strategy : AveragingStrategies.all()) {
            options.add(new AveragingOption(strategy.getId(), strategy.getName()));
        }
        final OptionalDouble grade = scheme.weightedGrade(gradebook);
        final Double weightedGrade = grade.isPresent() ? grade.getAsDouble() : null;
        return new CalculateGradeOutputData(gradebook.getCourseId(), gradebook.getCourseName(),
                rows, options, schemeSaved, weightedGrade, scheme.gradedWeight(gradebook));
    }

    /**
     * Explains how far the weights are from 100%, for example "The weights add
     * up to 95%. They need to add up to 100% (5% missing)."
     *
     * @param total the sum of the weights the student entered
     * @return the message to show
     */
    static String weightTotalMessage(double total) {
        final DecimalFormat format =
                new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
        format.setRoundingMode(RoundingMode.HALF_UP);
        final String direction;
        if (total < MAX_WEIGHT) {
            direction = "missing";
        }
        else {
            direction = "too much";
        }
        return "The weights add up to " + format.format(total) + "%. They need to add up to 100% ("
                + format.format(Math.abs(MAX_WEIGHT - total)) + "% " + direction + ").";
    }
}
