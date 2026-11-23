package use_case.read_announcements;

import java.time.Instant;

/** One announcement prepared for display, decoupled from the entity. */
public final class AnnouncementSummary {
    private final String course;
    private final String title;
    private final String message;
    private final Instant postedAt;
    private final boolean unread;

    /** Creates a display summary for a single announcement. */
    public AnnouncementSummary(final String course, final String title, final String message,
                               final Instant postedAt, final boolean unread) {
        this.course = course;
        this.title = title;
        this.message = message;
        this.postedAt = postedAt;
        this.unread = unread;
    }

    public String getCourse() {
        return course;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public boolean isUnread() {
        return unread;
    }
}
