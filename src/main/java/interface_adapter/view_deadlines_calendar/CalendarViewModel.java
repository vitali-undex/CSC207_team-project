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

    public CalendarViewModel(LocalDate today) {
        month = YearMonth.from(today);
        selectedDate = today;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void beginLoad(LocalDate date) {
        month = YearMonth.from(date);
        selectedDate = date;
        deadlines = Map.of();
        status = "Loading Quercus deadlines…";
        loading = true;
        changes.firePropertyChange("calendar", null, this);
    }

    public void showMonth(YearMonth loadedMonth, Map<LocalDate, List<DeadlineSummary>> loadedDeadlines,
                          String message) {
        if (month.equals(loadedMonth)) {
            deadlines = loadedDeadlines;
            status = message;
            loading = false;
            changes.firePropertyChange("calendar", null, this);
        }
    }

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
