package use_case.read_announcements;

import java.time.Instant;

/** The request data for loading announcements. */
public final class ReadAnnouncementsInputData {
    private final Instant lastVisit;

    /** Creates request data; announcements posted after {@code lastVisit} are marked new. */
    public ReadAnnouncementsInputData(final Instant lastVisit) {
        this.lastVisit = lastVisit;
    }

    public Instant getLastVisit() {
        return lastVisit;
    }
}
