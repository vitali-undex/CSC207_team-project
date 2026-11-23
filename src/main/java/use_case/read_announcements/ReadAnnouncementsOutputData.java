package use_case.read_announcements;

import java.util.List;

/** The announcements to show, already sorted newest first. */
public final class ReadAnnouncementsOutputData {
    private final List<AnnouncementSummary> announcements;

    /** Wraps the prepared announcements for the presenter. */
    public ReadAnnouncementsOutputData(final List<AnnouncementSummary> announcements) {
        this.announcements = List.copyOf(announcements);
    }

    public List<AnnouncementSummary> getAnnouncements() {
        return announcements;
    }
}
