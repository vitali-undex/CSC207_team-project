package interface_adapter.read_announcements;

import use_case.read_announcements.ReadAnnouncementsInputBoundary;
import use_case.read_announcements.ReadAnnouncementsInputData;

import java.time.Instant;

/** Turns a view request into a call on the announcements use case. */
public final class ReadAnnouncementsController {
    private final ReadAnnouncementsInputBoundary interactor;
    private final Instant lastVisit;

    /** Creates a controller that marks announcements after {@code lastVisit} as new. */
    public ReadAnnouncementsController(final ReadAnnouncementsInputBoundary interactor,
                                       final Instant lastVisit) {
        this.interactor = interactor;
        this.lastVisit = lastVisit;
    }

    /** Loads the announcements for the student. */
    public void load() {
        interactor.execute(new ReadAnnouncementsInputData(lastVisit));
    }
}
