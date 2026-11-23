package data_access;

import entity.Announcement;
import use_case.read_announcements.ReadAnnouncementsDataAccessInterface;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * A stand-in announcement source with hard-coded data, so the app runs without a Quercus
 * token or a network connection. The real {@code CanvasApiDataAccessObject} will implement the
 * same interface and can be dropped in without touching the use case.
 */
public final class DemoAnnouncementsDataAccessObject implements ReadAnnouncementsDataAccessInterface {
    @Override
    public List<Announcement> loadAnnouncements() {
        final Instant now = Instant.now();
        return List.of(
                new Announcement("1", "CSC207", "Project milestone 3 due Nov 27",
                        "Each interactor needs at least one unit test by the milestone.",
                        now.minus(Duration.ofHours(3)), false),
                new Announcement("2", "MAT237", "Term test 2 room change",
                        "Our section writes in EX 300, not the usual lecture hall.",
                        now.minus(Duration.ofDays(1)), false),
                new Announcement("3", "STA238", "Assignment 4 solutions posted",
                        "Solutions are under Files. Regrade requests close Friday.",
                        now.minus(Duration.ofDays(4)), false));
    }
}
