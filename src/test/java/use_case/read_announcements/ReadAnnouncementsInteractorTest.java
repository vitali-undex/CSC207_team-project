package use_case.read_announcements;

import entity.Announcement;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit tests for {@link ReadAnnouncementsInteractor}. */
class ReadAnnouncementsInteractorTest {
    private static final Instant BASE = Instant.parse("2026-11-20T00:00:00Z");

    @Test
    void sortsNewestFirstAndMarksNewSinceLastVisit() {
        final List<Announcement> data = List.of(
                new Announcement("1", "MAT237", "old", "body", BASE, false),
                new Announcement("2", "CSC207", "new", "body", BASE.plusSeconds(3600), false));
        final CapturingPresenter presenter = new CapturingPresenter();
        final ReadAnnouncementsInteractor interactor =
                new ReadAnnouncementsInteractor(() -> data, presenter);

        interactor.execute(new ReadAnnouncementsInputData(BASE.plusSeconds(600)));

        final List<AnnouncementSummary> result = presenter.output.getAnnouncements();
        assertEquals("new", result.get(0).getTitle());
        assertTrue(result.get(0).isUnread());
        assertEquals("old", result.get(1).getTitle());
        assertFalse(result.get(1).isUnread());
    }

    @Test
    void reportsFailureWhenSourceThrows() {
        final CapturingPresenter presenter = new CapturingPresenter();
        final ReadAnnouncementsInteractor interactor = new ReadAnnouncementsInteractor(() -> {
            throw new java.io.IOException("offline");
        }, presenter);

        interactor.execute(new ReadAnnouncementsInputData(BASE));

        assertTrue(presenter.failed);
    }

    /** Captures whatever the interactor presents, for assertions. */
    private static final class CapturingPresenter implements ReadAnnouncementsOutputBoundary {
        private ReadAnnouncementsOutputData output;
        private boolean failed;

        @Override
        public void prepareSuccessView(final ReadAnnouncementsOutputData outputData) {
            this.output = outputData;
        }

        @Override
        public void prepareFailView(final String message) {
            this.failed = true;
        }
    }
}
