package use_case.view_deadlines_calendar;

import entity.Assignment;
import entity.Course;
import use_case.view_deadlines_calendar.ViewDeadlinesCalendarOutputData.Source;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Loads deadlines from Canvas or the cache and organizes them by local date. */
public final class ViewDeadlinesCalendarInteractor implements ViewDeadlinesCalendarInputBoundary {
    private final CalendarAssignmentDataAccessInterface online;
    private final CalendarCacheDataAccessInterface cache;
    private final ViewDeadlinesCalendarOutputBoundary presenter;

    /** Connects the online source, local cache, and presenter for the calendar use case. */
    public ViewDeadlinesCalendarInteractor(CalendarAssignmentDataAccessInterface online,
                                           CalendarCacheDataAccessInterface cache,
                                           ViewDeadlinesCalendarOutputBoundary presenter) {
        this.online = online;
        this.cache = cache;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewDeadlinesCalendarInputData inputData) {
        final CalendarSnapshot snapshot;
        try {
            snapshot = online.loadAssignments();
        }
        catch (IOException exception) {
            presentCachedMonth(inputData);
            return;
        }

        Source source = Source.LIVE;
        try {
            cache.saveSnapshot(snapshot);
        }
        catch (IOException exception) {
            source = Source.LIVE_WITHOUT_BACKUP;
        }
        presentMonth(inputData, snapshot, source);
    }

    private void presentCachedMonth(ViewDeadlinesCalendarInputData inputData) {
        try {
            final Optional<CalendarSnapshot> snapshot = cache.readSnapshot();
            if (snapshot.isPresent()) {
                presentMonth(inputData, snapshot.get(), Source.CACHED);
            }
            else {
                presenter.prepareFailView(inputData.getMonth(),
                        "Could not load Quercus deadlines. There is no saved copy.");
            }
        }
        catch (IOException exception) {
            presenter.prepareFailView(inputData.getMonth(),
                    "Could not load Quercus deadlines or read the saved copy.");
        }
    }

    private void presentMonth(ViewDeadlinesCalendarInputData inputData,
                              CalendarSnapshot snapshot, Source source) {
        final Map<String, String> courseCodes = new HashMap<>();
        for (Course course : snapshot.getCourses()) {
            courseCodes.put(course.getId(), course.getCode());
        }

        final Map<LocalDate, List<DeadlineSummary>> byDay = new HashMap<>();
        for (Assignment assignment : snapshot.getAssignments()) {
            if (assignment.getDueAt() != null) {
                final ZonedDateTime due = assignment.getDueAt().atZone(inputData.getZone());
                if (YearMonth.from(due).equals(inputData.getMonth())) {
                    final String course = courseCodes.getOrDefault(assignment.getCourseId(),
                            assignment.getCourseId());
                    final DeadlineSummary deadline =
                            new DeadlineSummary(course, assignment.getName(), due,
                                assignment.getPointsPossible());
                    byDay.computeIfAbsent(due.toLocalDate(),
                        date -> new ArrayList<>()).add(deadline);
                }
            }
        }
        byDay.replaceAll((date, deadlines) -> {
            deadlines.sort(Comparator.comparing((DeadlineSummary deadline) ->
                    deadline.getDueAt().toInstant())
                    .thenComparing(DeadlineSummary::getCourse)
                    .thenComparing(DeadlineSummary::getTitle));
            return List.copyOf(deadlines);
        });
        presenter.prepareSuccessView(new ViewDeadlinesCalendarOutputData(inputData.getMonth(),
                byDay, snapshot.getSyncedAt().atZone(inputData.getZone()), source));
    }
}
