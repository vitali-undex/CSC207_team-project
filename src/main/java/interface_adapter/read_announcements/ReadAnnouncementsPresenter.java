package interface_adapter.read_announcements;

import use_case.read_announcements.ReadAnnouncementsOutputBoundary;
import use_case.read_announcements.ReadAnnouncementsOutputData;

/** Passes use case results to the view model. */
public final class ReadAnnouncementsPresenter implements ReadAnnouncementsOutputBoundary {
    private final AnnouncementsViewModel viewModel;

    /** Creates a presenter that updates the given view model. */
    public ReadAnnouncementsPresenter(final AnnouncementsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(final ReadAnnouncementsOutputData outputData) {
        viewModel.setAnnouncements(outputData.getAnnouncements());
    }

    @Override
    public void prepareFailView(final String message) {
        viewModel.setError(message);
    }
}
