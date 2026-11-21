package view;

import org.junit.jupiter.api.Test;
import app.CalendarAppBuilder;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;

class CalendarViewTest {
    @Test
    void opensOnRequestedDateAndRespondsToYearSpinner() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Clock clock = Clock.fixed(Instant.parse("2027-12-31T12:00:00Z"), ZoneOffset.UTC);
            CalendarView calendar = new CalendarAppBuilder().buildPreview(clock);
            calendar.open(LocalDate.of(2028, 2, 29));
            assertEquals(LocalDate.of(2028, 2, 29), calendar.getSelectedDate());
            JSpinner year = findSpinner(calendar);
            assertNotNull(year);
            year.setValue(2029);
            assertEquals(LocalDate.of(2029, 2, 1), calendar.getSelectedDate());
        });
    }

    private static JSpinner findSpinner(Container parent) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JSpinner) {
                return (JSpinner) child;
            }
            if (child instanceof Container) {
                JSpinner spinner = findSpinner((Container) child);
                if (spinner != null) {
                    return spinner;
                }
            }
        }
        return null;
    }
}
