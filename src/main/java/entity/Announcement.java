package entity;

import java.time.Instant;

/** A single course announcement loaded from Quercus. */
public final class Announcement {
    private final String id;
    private final String courseCode;
    private final String title;
    private final String message;
    private final Instant postedAt;
    private boolean read;

    /** Creates an announcement; {@code read} is false until the student opens it. */
    public Announcement(final String id, final String courseCode, final String title,
                        final String message, final Instant postedAt, final boolean read) {
        this.id = id;
        this.courseCode = courseCode;
        this.title = title;
        this.message = message;
        this.postedAt = postedAt;
        this.read = read;
    }

    /** Marks this announcement as read. */
    public void markRead() {
        this.read = true;
    }

    public String getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
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

    public boolean isRead() {
        return read;
    }
}
