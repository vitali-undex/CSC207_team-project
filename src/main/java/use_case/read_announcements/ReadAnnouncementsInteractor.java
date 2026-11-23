package use_case.read_announcements;

import entity.Announcement;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Loads announcements, sorts them newest first, and marks the ones new since the last visit. */
public final class ReadAnnouncementsInteractor implements ReadAnnouncementsInputBoundary {
    private final ReadAnnouncementsDataAccessInterface dataAccess;
    private final ReadAnnouncementsOutputBoundary presenter;

    /** Connects the announcement source and the presenter. */
    public ReadAnnouncementsInteractor(final ReadAnnouncementsDataAccessInterface dataAccess,
                                       final ReadAnnouncementsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(final ReadAnnouncementsInputData inputData) {
        final List<Announcement> loaded;
        try {
            loaded = dataAccess.loadAnnouncements();
        }
        catch (IOException exception) {
            presenter.prepareFailView("Could not load announcements from Quercus.");
            return;
        }

        final Instant lastVisit = inputData.getLastVisit();
        final List<AnnouncementSummary> summaries = new ArrayList<>();
        for (final Announcement announcement : loaded) {
            final boolean unread = announcement.getPostedAt().isAfter(lastVisit);
            summaries.add(new AnnouncementSummary(announcement.getCourseCode(),
                    announcement.getTitle(), announcement.getMessage(),
                    announcement.getPostedAt(), unread));
        }
        summaries.sort(Comparator.comparing(AnnouncementSummary::getPostedAt).reversed()
                .thenComparing(AnnouncementSummary::getCourse));

        presenter.prepareSuccessView(new ReadAnnouncementsOutputData(summaries));
    }
}
