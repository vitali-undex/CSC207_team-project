package app;

import data_access.DemoAnnouncementsDataAccessObject;
import interface_adapter.read_announcements.AnnouncementsViewModel;
import interface_adapter.read_announcements.ReadAnnouncementsController;
import interface_adapter.read_announcements.ReadAnnouncementsPresenter;
import use_case.read_announcements.ReadAnnouncementsDataAccessInterface;
import use_case.read_announcements.ReadAnnouncementsInputBoundary;
import use_case.read_announcements.ReadAnnouncementsInteractor;
import use_case.read_announcements.ReadAnnouncementsOutputBoundary;
import view.AnnouncementsView;

import javax.swing.JFrame;
import java.time.Duration;
import java.time.Instant;

/** Wires the announcements use case together and builds its window. */
public final class AnnouncementsAppBuilder {
    private AnnouncementsAppBuilder() {
    }

    /** Creates every object for the use case and returns a ready-to-show frame. */
    public static JFrame build() {
        final ReadAnnouncementsDataAccessInterface dataAccess =
                new DemoAnnouncementsDataAccessObject();
        final AnnouncementsViewModel viewModel = new AnnouncementsViewModel();

        final ReadAnnouncementsOutputBoundary presenter =
                new ReadAnnouncementsPresenter(viewModel);
        final ReadAnnouncementsInputBoundary interactor =
                new ReadAnnouncementsInteractor(dataAccess, presenter);

        // Pretend the student last opened the app two days ago, so recent posts show as new.
        final Instant lastVisit = Instant.now().minus(Duration.ofDays(2));
        final ReadAnnouncementsController controller =
                new ReadAnnouncementsController(interactor, lastVisit);

        final AnnouncementsView view = new AnnouncementsView(controller, viewModel);
        controller.load();

        final JFrame frame = new JFrame("Quercus Announcements");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 420);
        frame.setLocationRelativeTo(null);
        frame.add(view);
        return frame;
    }
}
