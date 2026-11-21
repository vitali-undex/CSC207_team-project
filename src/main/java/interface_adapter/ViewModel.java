package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The base class for every view model, as in the ca-login lab code. A view
 * model holds the state its view shows, and tells the view when it changes.
 *
 * @param <T> the type of state this view model holds
 */
public class ViewModel<T> {

    private final String viewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private T state;

    /**
     * Creates a view model.
     *
     * @param viewName the name of the view, also used as its tab title
     */
    public ViewModel(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public T getState() {
        return state;
    }

    public void setState(T state) {
        this.state = state;
    }

    /** Tells the listeners that the state changed. */
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, state);
    }

    /**
     * Tells the listeners that the state changed, naming what happened.
     *
     * @param propertyName what happened, so the view can react to it
     */
    public void firePropertyChanged(String propertyName) {
        support.firePropertyChange(propertyName, null, state);
    }

    /**
     * Adds a listener, normally the view.
     *
     * @param listener the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
