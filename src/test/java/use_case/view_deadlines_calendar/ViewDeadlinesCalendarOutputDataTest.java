package use_case.view_deadlines_calendar;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ViewDeadlinesCalendarOutputDataTest {
    @Test
    void preservesMonthSyncMetadataAndAnImmutableDateIndex() {
        final YearMonth month = YearMonth.of(2028, 2);
        final ZonedDateTime syncedAt =
            ZonedDateTime.parse("2028-02-28T12:00:00-05:00[America/Toronto]");
        final DeadlineSummary deadline =
            new DeadlineSummary("CSC207", "Quiz", syncedAt.plusDays(1), null);
        final LocalDate leapDay = month.atEndOfMonth();
        final Map<LocalDate, List<DeadlineSummary>> dates = new HashMap<>();
        dates.put(leapDay, List.of(deadline));

        final ViewDeadlinesCalendarOutputData result = new ViewDeadlinesCalendarOutputData(
                month, dates, syncedAt, ViewDeadlinesCalendarOutputData.Source.CACHED);
        dates.clear();
        assertEquals(List.of(deadline), result.getDeadlines().get(leapDay));
        assertThrows(UnsupportedOperationException.class, () -> result.getDeadlines().clear());
        assertEquals(month, result.getMonth());
        assertEquals(syncedAt, result.getSyncedAt());
        assertEquals(ViewDeadlinesCalendarOutputData.Source.CACHED, result.getSource());
    }
}
