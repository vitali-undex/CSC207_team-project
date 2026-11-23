package app;

import interface_adapter.calculate_grade.GradeCalculatorController;
import interface_adapter.calculate_grade.GradeCalculatorPresenter;
import interface_adapter.calculate_grade.GradeCalculatorViewModel;
import use_case.calculate_grade.CalculateGradeInputBoundary;
import use_case.calculate_grade.CalculateGradeInteractor;
import use_case.calculate_grade.GradeSourceDataAccessInterface;
import use_case.calculate_grade.GradingSchemeDataAccessInterface;
import view.GradeCalculatorView;

/**
 * Wires the grade calculator's layers together. This is the only place that
 * knows which data access classes are used, so the demo data can be swapped
 * for a Quercus source here without changing anything else.
 */
public final class GradeCalculatorAppBuilder {

    /**
     * Builds the grade calculator screen.
     *
     * @param gradeSource where courses and grades come from
     * @param schemeStore where grading schemes are saved
     * @return the screen, ready to be opened
     */
    public GradeCalculatorView build(GradeSourceDataAccessInterface gradeSource,
                                     GradingSchemeDataAccessInterface schemeStore) {
        final GradeCalculatorViewModel viewModel = new GradeCalculatorViewModel();
        final GradeCalculatorPresenter presenter = new GradeCalculatorPresenter(viewModel);
        final CalculateGradeInputBoundary interactor =
                new CalculateGradeInteractor(gradeSource, schemeStore, presenter);
        final GradeCalculatorController controller = new GradeCalculatorController(interactor);
        return new GradeCalculatorView(viewModel, controller);
    }
}
