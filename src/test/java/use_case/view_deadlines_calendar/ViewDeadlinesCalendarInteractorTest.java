package use_case.view_deadlines_calendar;

import entity.Assignment;
import entity.Course;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewDeadlinesCalendarInteractorTest {
    private static final YearMonth MONTH = YearMonth.of(2027, 2);
    private static final ZoneId ZONE = ZoneId.of("America/Toronto");
    private final TestCache cache = new TestCache();
    private final TestPresenter presenter = new TestPresenter();

    @Test
    void groupsByTorontoDateAndSortsAcrossCourses() {
        final CalendarSnapshot snapshot = snapshot();
        execute(() -> snapshot, MONTH);

        final List<DeadlineSummary> deadlines =
            presenter.output.getDeadlines().get(MONTH.atEndOfMonth());
        assertEquals(List.of("Early", "Late"), List.of(deadlines.get(0).getTitle(),
            deadlines.get(1).getTitle()));
        assertEquals("CSC207", deadlines.get(1).getCourse());
        assertEquals(new BigDecimal("15.5"), deadlines.get(1).getPointsPossible());
        assertNull(deadlines.get(0).getPointsPossible());
        assertEquals(LocalDate.of(2027, 2, 28), deadlines.get(1).getDueAt().toLocalDate());
        assertEquals(1, presenter.output.getDeadlines().size());
        assertEquals(ViewDeadlinesCalendarOutputData.Source.LIVE, presenter.output.getSource());
        assertEquals(1, cache.writes);
    }

    @Test
    void usesLastCompleteSnapshotAndSupportsOfflineMonthNavigation() {
        cache.snapshot = snapshot();
        execute(ViewDeadlinesCalendarInteractorTest::offline, MONTH.plusMonths(1));

        assertEquals("March assignment",
                presenter.output.getDeadlines().get(LocalDate.of(2027, 3, 1)).get(0).getTitle());
        assertEquals(ViewDeadlinesCalendarOutputData.Source.CACHED, presenter.output.getSource());
        assertEquals(cache.snapshot.getSyncedAt(), presenter.output.getSyncedAt().toInstant());
        assertEquals(0, cache.writes);
    }

    @Test
    void distinguishesAnEmptyMonthFromAFirstOfflineLoad() {
        execute(ViewDeadlinesCalendarInteractorTest::offline, MONTH);
        assertNull(presenter.output);
        assertTrue(presenter.failure.contains("no saved copy"));

        execute(ViewDeadlinesCalendarInteractorTest::snapshot, MONTH.plusYears(1));
        assertTrue(presenter.output.getDeadlines().isEmpty());
    }

    @Test
    void showsLiveDeadlinesWhenTheBackupCannotBeSaved() {
        cache.failWrite = true;
        execute(ViewDeadlinesCalendarInteractorTest::snapshot, MONTH);
        assertEquals(ViewDeadlinesCalendarOutputData.Source.LIVE_WITHOUT_BACKUP,
            presenter.output.getSource());
        assertEquals(2, presenter.output.getDeadlines().get(MONTH.atEndOfMonth()).size());
    }

    private void execute(CalendarAssignmentDataAccessInterface online, YearMonth month) {
        new ViewDeadlinesCalendarInteractor(online, cache, presenter)
                .execute(new ViewDeadlinesCalendarInputData(month, ZONE));
    }

    private static CalendarSnapshot offline() throws IOException {
        throw new IOException("Offline");
    }

    private static CalendarSnapshot snapshot() {
        return new CalendarSnapshot(List.of(new Course("a", "CSC207", "Software Design"),
                new Course("b", "CSC236", "Theory")), List.of(
                    new Assignment("1", "a", "Late", Instant.parse("2027-03-01T04:59:00Z"),
                        new BigDecimal("15.5")),
                    new Assignment("2", "b", "Early", Instant.parse("2027-02-28T20:00:00Z"), null),
                    new Assignment("3", "a", "March assignment",
                        Instant.parse("2027-03-01T05:00:00Z"), null),
                    new Assignment("4", "b", "Undated", null, null)),
                    Instant.parse("2027-02-26T15:00:00Z"));
    }

    private static final class TestCache implements CalendarCacheDataAccessInterface {
        private CalendarSnapshot snapshot;
        private int writes;
        private boolean failWrite;

        @Override
        public Optional<CalendarSnapshot> readSnapshot() {
            return Optional.ofNullable(snapshot);
        }

        @Override
        public void saveSnapshot(CalendarSnapshot saved) throws IOException {
            if (failWrite) {
                throw new IOException("Cache unavailable");
            }
            writes++;
            snapshot = saved;
        }
    }

    private static final class TestPresenter implements ViewDeadlinesCalendarOutputBoundary {
        private ViewDeadlinesCalendarOutputData output;
        private String failure;

        @Override
        public void prepareSuccessView(ViewDeadlinesCalendarOutputData outputData) {
            output = outputData;
        }

        @Override
        public void prepareFailView(YearMonth month, String message) {
            failure = message;
        }
    }
}
