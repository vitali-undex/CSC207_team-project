package interface_adapter.view_deadlines_calendar;

import use_case.view_deadlines_calendar.DeadlineSummary;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/** Calendar screen state. Reads and writes take place on Swing's event dispatch thread. */
public final class CalendarViewModel {
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private YearMonth month;
    private LocalDate selectedDate;
    private Map<LocalDate, List<DeadlineSummary>> deadlines = Map.of();
    private String status = "";
    private boolean loading;

    /** Initializes the calendar with today selected. */
    public CalendarViewModel(LocalDate today) {
        month = YearMonth.from(today);
        selectedDate = today;
    }

    /** Registers a listener for calendar state changes. */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    /** Selects the date and marks its month as loading. */
    public void beginLoad(LocalDate date) {
        showDate(date, "Loading Quercus deadlines…", true);
    }

    /** Navigates to a date in the disconnected calendar. */
    public void browseDate(LocalDate date) {
        showDate(date, "", false);
    }

    private void showDate(LocalDate date, String message, boolean loadingMonth) {
        month = YearMonth.from(date);
        selectedDate = date;
        deadlines = Map.of();
        status = message;
        loading = loadingMonth;
        changes.firePropertyChange("calendar", null, this);
    }

    /** Applies deadlines and a status message if the loaded month is still displayed. */
    public void showMonth(
            YearMonth loadedMonth, Map<LocalDate,
            List<DeadlineSummary>> loadedDeadlines,
                          String message) {
        if (month.equals(loadedMonth)) {
            deadlines = loadedDeadlines;
            status = message;
            loading = false;
            changes.firePropertyChange("calendar", null, this);
        }
    }

    /** Selects a date if it belongs to the displayed month. */
    public void selectDate(LocalDate date) {
        if (month.equals(YearMonth.from(date))) {
            final LocalDate oldDate = selectedDate;
            selectedDate = date;
            changes.firePropertyChange("selectedDate", oldDate, date);
        }
    }

    public YearMonth getMonth() {
        return month;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /** Returns the deadlines for the date, or an empty list if there are none. */
    public List<DeadlineSummary> getDeadlines(LocalDate date) {
        return deadlines.getOrDefault(date, List.of());
    }

    public String getStatus() {
        return status;
    }

    public boolean isLoading() {
        return loading;
    }
}
