package interface_adapter.read_announcements;

import use_case.read_announcements.AnnouncementSummary;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/** Holds the announcements state and notifies the view when it changes. */
public final class AnnouncementsViewModel {
    /** Property name fired whenever the state changes. */
    public static final String STATE = "state";

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private List<AnnouncementSummary> announcements = new ArrayList<>();
    private String error = "";

    /** Replaces the announcements and clears any error, then notifies listeners. */
    public void setAnnouncements(final List<AnnouncementSummary> newAnnouncements) {
        this.announcements = newAnnouncements;
        this.error = "";
        support.firePropertyChange(STATE, null, this);
    }

    /** Records an error message and notifies listeners. */
    public void setError(final String message) {
        this.error = message;
        support.firePropertyChange(STATE, null, this);
    }

    public List<AnnouncementSummary> getAnnouncements() {
        return announcements;
    }

    public String getError() {
        return error;
    }

    /** Registers a listener for state changes. */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
