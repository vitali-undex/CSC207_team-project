package use_case.read_announcements;

/** Receives the results of an announcements load. */
public interface ReadAnnouncementsOutputBoundary {
    /** Presents the loaded announcements, newest first. */
    void prepareSuccessView(ReadAnnouncementsOutputData outputData);

    /** Presents a load error. */
    void prepareFailView(String message);
}
