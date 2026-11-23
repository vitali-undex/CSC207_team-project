package use_case.read_announcements;

/** Defines the request to load announcements. */
public interface ReadAnnouncementsInputBoundary {
    /** Loads announcements and marks the ones new since the student's last visit. */
    void execute(ReadAnnouncementsInputData inputData);
}
