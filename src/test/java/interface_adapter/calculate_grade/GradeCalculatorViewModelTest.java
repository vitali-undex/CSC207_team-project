package interface_adapter.calculate_grade;

import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeCalculatorViewModelTest {

    @Test
    void tellsListenersWhichPartChanged() {
        GradeCalculatorViewModel viewModel = new GradeCalculatorViewModel();
        List<PropertyChangeEvent> events = new ArrayList<>();
        viewModel.addPropertyChangeListener(events::add);
        GradeCalculatorState state = new GradeCalculatorState();

        viewModel.setState(state);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.ERROR_PROPERTY);
        viewModel.firePropertyChanged(GradeCalculatorViewModel.ERROR_PROPERTY);

        assertSame(state, viewModel.getState());
        assertEquals(2, events.size());
        assertEquals(GradeCalculatorViewModel.ERROR_PROPERTY, events.get(0).getPropertyName());
        assertSame(state, events.get(1).getNewValue());
    }

    @Test
    void stateStartsEmptyAndKeepsItsOwnLists() {
        GradeCalculatorState state = new GradeCalculatorState();
        assertTrue(state.getCourses().isEmpty());
        assertTrue(state.getRows().isEmpty());
        assertEquals("", state.getCourseId());

        List<Choice> courses = new ArrayList<>(List.of(new Choice("course-1", "Course One")));
        state.setCourses(courses);
        courses.clear();

        assertEquals(1, state.getCourses().size());
        assertThrows(UnsupportedOperationException.class, () -> state.getCourses().clear());
    }
}
