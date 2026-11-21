package interface_adapter.calculate_grade;

import use_case.calculate_grade.AveragingOption;
import use_case.calculate_grade.CalculateGradeOutputBoundary;
import use_case.calculate_grade.CalculateGradeOutputData;
import use_case.calculate_grade.CourseOption;
import use_case.calculate_grade.GroupResult;
import use_case.calculate_grade.GroupSettings;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Turns the calculate grade use case's results into text for the grade
 * calculator screen.
 */
public class GradeCalculatorPresenter implements CalculateGradeOutputBoundary {

    /** Shown under a course that has no saved grading scheme yet. */
    public static final String ENTER_WEIGHTS_MESSAGE =
            "Enter each group's weight from the syllabus, then press Calculate grade.";

    /** Shown in place of an average when nothing in a group is graded. */
    public static final String NO_AVERAGE = "—";

    private final GradeCalculatorViewModel viewModel;
    private final DecimalFormat numberFormat =
            new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));

    /**
     * Creates the presenter.
     *
     * @param viewModel the view model to update
     */
    public GradeCalculatorPresenter(GradeCalculatorViewModel viewModel) {
        this.viewModel = viewModel;
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    @Override
    public void prepareCourseListView(List<CourseOption> courses) {
        final List<Choice> choices = new ArrayList<>();
        for (CourseOption course : courses) {
            choices.add(new Choice(course.getId(), course.getName()));
        }
        final GradeCalculatorState state = viewModel.getState();
        state.setCourses(choices);
        state.setErrorMessage("");
        viewModel.setState(state);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.COURSES_PROPERTY);
    }

    @Override
    public void prepareCourseView(CalculateGradeOutputData outputData) {
        if (outputData.isSchemeSaved()) {
            showBreakdown(outputData, gradeText(outputData),
                    basisText(outputData) + " Using the weights you saved.");
        }
        else {
            showBreakdown(outputData, "", ENTER_WEIGHTS_MESSAGE);
        }
    }

    @Override
    public void prepareSuccessView(CalculateGradeOutputData outputData) {
        showBreakdown(outputData, gradeText(outputData), basisText(outputData) + " Weights saved.");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final GradeCalculatorState state = viewModel.getState();
        state.setGradeText("");
        state.setDetailText("");
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.ERROR_PROPERTY);
    }

    @Override
    public void prepareLoadFailView(String errorMessage) {
        final GradeCalculatorState state = viewModel.getState();
        state.setCourseId("");
        state.setCourseName("");
        state.setRows(List.of());
        state.setGradeText("");
        state.setDetailText("");
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.STATE_PROPERTY);
    }

    private void showBreakdown(CalculateGradeOutputData outputData, String gradeText,
                               String detailText) {
        final List<Choice> averagingChoices = new ArrayList<>();
        for (AveragingOption option : outputData.getAveragingOptions()) {
            averagingChoices.add(new Choice(option.getId(), option.getName()));
        }
        final List<GroupRowState> rows = new ArrayList<>();
        for (GroupResult group : outputData.getGroups()) {
            final GroupSettings settings = group.getSettings();
            rows.add(new GroupRowState(group.getGroupId(), group.getName(),
                    numberFormat.format(settings.getWeightPercent()),
                    choiceFor(settings.getAveragingId(), averagingChoices),
                    Integer.toString(settings.getDropLowest()), keepBestText(settings),
                    settings.isCapAtFullMarks(), averageText(group),
                    group.getGradedCount() + " of " + group.getAssignmentCount()));
        }
        final GradeCalculatorState state = viewModel.getState();
        state.setCourseId(outputData.getCourseId());
        state.setCourseName(outputData.getCourseName());
        state.setAveragingChoices(averagingChoices);
        state.setRows(rows);
        state.setGradeText(gradeText);
        state.setDetailText(detailText);
        state.setErrorMessage("");
        viewModel.setState(state);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.STATE_PROPERTY);
    }

    private static Choice choiceFor(String averagingId, List<Choice> averagingChoices) {
        for (Choice choice : averagingChoices) {
            if (choice.getId().equals(averagingId)) {
                return choice;
            }
        }
        return new Choice(averagingId, averagingId);
    }

    private static String keepBestText(GroupSettings settings) {
        if (settings.getKeepBest() == 0) {
            return "";
        }
        return Integer.toString(settings.getKeepBest());
    }

    private String averageText(GroupResult group) {
        if (group.getAveragePercent() == null) {
            return NO_AVERAGE;
        }
        return numberFormat.format(group.getAveragePercent()) + "%";
    }

    private String gradeText(CalculateGradeOutputData outputData) {
        if (outputData.getWeightedGrade() == null) {
            return "No grade yet";
        }
        return "Current grade: " + numberFormat.format(outputData.getWeightedGrade()) + "%";
    }

    private String basisText(CalculateGradeOutputData outputData) {
        if (outputData.getWeightedGrade() == null) {
            return "Nothing with a weight has been graded yet.";
        }
        return "Based on " + numberFormat.format(outputData.getGradedWeight())
                + "% of the course graded so far.";
    }
}
