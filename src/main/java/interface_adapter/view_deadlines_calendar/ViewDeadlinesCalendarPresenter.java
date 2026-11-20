package interface_adapter.view_deadlines_calendar;

import use_case.view_deadlines_calendar.ViewDeadlinesCalendarOutputBoundary;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarOutputData;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Executor;

public final class ViewDeadlinesCalendarPresenter implements ViewDeadlinesCalendarOutputBoundary {
    private static final DateTimeFormatter SYNC_TIME = DateTimeFormatter.ofPattern("MMM d, uuuu 'at' h:mm a z");

    private final CalendarViewModel viewModel;
    private final Executor uiExecutor;

    public ViewDeadlinesCalendarPresenter(CalendarViewModel viewModel, Executor uiExecutor) {
        this.viewModel = viewModel;
        this.uiExecutor = uiExecutor;
    }

    @Override
    public void prepareSuccessView(ViewDeadlinesCalendarOutputData outputData) {
        final String syncTime = SYNC_TIME.format(outputData.getSyncedAt());
        final String sourceMessage;
        switch (outputData.getSource()) {
            case CACHED:
                sourceMessage = "Showing saved deadlines · Last synced " + syncTime;
                break;
            case LIVE_WITHOUT_BACKUP:
                sourceMessage = "Synced " + syncTime + " · Could not save for offline use";
                break;
            default:
                sourceMessage = "Synced " + syncTime;
                break;
        }
        final String message = outputData.getDeadlines().isEmpty()
                ? "No deadlines this month · " + sourceMessage : sourceMessage;
        uiExecutor.execute(() -> viewModel.showMonth(outputData.getMonth(), outputData.getDeadlines(), message));
    }

    @Override
    public void prepareFailView(YearMonth month, String message) {
        uiExecutor.execute(() -> viewModel.showMonth(month, Map.of(), message));
    }
}
