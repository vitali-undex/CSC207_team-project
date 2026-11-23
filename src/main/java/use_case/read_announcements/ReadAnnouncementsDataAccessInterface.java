package use_case.read_announcements;

import entity.Announcement;

import java.io.IOException;
import java.util.List;

/** Source of announcements for the connected Quercus account. */
public interface ReadAnnouncementsDataAccessInterface {
    /** Loads recent announcements across all of the student's active courses. */
    List<Announcement> loadAnnouncements() throws IOException;
}
