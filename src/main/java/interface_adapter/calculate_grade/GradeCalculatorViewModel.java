package interface_adapter.calculate_grade;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Holds the grade calculator screen's state and tells the view when it
 * changes (the Observer pattern). The property name says what changed, so the
 * view only redraws that part.
 */
public class GradeCalculatorViewModel {

    /** The course list changed. */
    public static final String COURSES_PROPERTY = "courses";

    /** The course breakdown changed: its rows, grade and messages. */
    public static final String STATE_PROPERTY = "state";

    /** Only the messages changed; the rows keep what the student typed. */
    public static final String ERROR_PROPERTY = "error";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private GradeCalculatorState state = new GradeCalculatorState();

    public GradeCalculatorState getState() {
        return state;
    }

    public void setState(GradeCalculatorState state) {
        this.state = state;
    }

    /**
     * Tells the listeners that part of the state changed.
     *
     * @param propertyName which part changed, one of the property constants
     */
    public void firePropertyChanged(String propertyName) {
        support.firePropertyChange(propertyName, null, state);
    }

    /**
     * Adds a listener to be told about changes.
     *
     * @param listener the listener, usually the view
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
