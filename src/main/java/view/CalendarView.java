package view;

import interface_adapter.view_deadlines_calendar.CalendarViewModel;
import interface_adapter.view_deadlines_calendar.ViewDeadlinesCalendarController;
import use_case.view_deadlines_calendar.DeadlineSummary;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/** Swing month grid and deadline details for one selected date. */
public final class CalendarView extends JPanel {
    private static final Color BACKGROUND = new Color(0xf1f5fa);
    private static final Color WHITE = Color.WHITE;
    private static final Color INK = new Color(0x172b46);
    private static final Color MUTED = new Color(0x495e76);
    private static final Color ACCENT = new Color(0x2459a8);
    private static final Color SELECTED = new Color(0xdceaff);
    private static final Color BORDER = new Color(0xcbd6e4);
    private static final int FIRST_YEAR = 1;
    private static final int LAST_YEAR = 9999;
    private static final int DAYS_PER_WEEK = 7;
    private static final int WEEKS = 6;
    private static final int MAX_PREVIEWS = 2;
    private static final DateTimeFormatter DAY_TITLE =
            DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DUE_TIME =
            DateTimeFormatter.ofPattern("h:mm a z", Locale.ENGLISH);

    private final CalendarViewModel model;
    private final ViewDeadlinesCalendarController controller;
    private final Clock clock;
    private final boolean connected;
    private final JButton previous = new JButton("‹");
    private final JButton next = new JButton("›");
    private final JButton today = new JButton("Today");
    private final JButton refresh = new JButton("Refresh");
    private final JComboBox<Month> month = new JComboBox<>(Month.values());
    private final JSpinner year;
    private final JPanel grid = new JPanel(new GridLayout(WEEKS, DAYS_PER_WEEK, 5, 5));
    private final JLabel status = new JLabel();
    private final JLabel dayTitle = new JLabel();
    private final JList<String> details = new JList<>();
    private final JButton[] days = new JButton[WEEKS * DAYS_PER_WEEK];
    private boolean updatingControls;

    /** Creates a calendar view for a connected account or a disconnected preview. */
    public CalendarView(CalendarViewModel model, ViewDeadlinesCalendarController controller,
                        Clock clock, boolean connected) {
        super(new BorderLayout(0, 16));
        this.model = model;
        this.controller = controller;
        this.clock = clock;
        this.connected = connected;
        year = new JSpinner(new SpinnerNumberModel(
                model.getMonth().getYear(), FIRST_YEAR, LAST_YEAR, 1));
        year.setEditor(new JSpinner.NumberEditor(year, "0"));
        year.getAccessibleContext().setAccessibleName("Year");
        month.getAccessibleContext().setAccessibleName("Month");
        previous.getAccessibleContext().setAccessibleName("Previous month");
        next.getAccessibleContext().setAccessibleName("Next month");

        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCalendar(), BorderLayout.CENTER);
        add(buildDetails(), BorderLayout.EAST);
        model.addPropertyChangeListener(event -> {
            if ("selectedDate".equals(event.getPropertyName())) {
                renderSelection();
            }
            else {
                render();
            }
        });
        render();
    }

    /** Opens the calendar with today selected. */
    public void open() {
        open(LocalDate.now(clock));
    }

    /** Opens the calendar on the given date. */
    public void open(LocalDate date) {
        loadDate(date);
    }

    public LocalDate getSelectedDate() {
        return model.getSelectedDate();
    }

    private JPanel buildHeader() {
        final JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        final JLabel title = new JLabel("Deadlines");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 29f));
        title.setForeground(INK);
        title.setAlignmentX(LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(6));

        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEADING, 7, 0));
        controls.setOpaque(false);
        controls.setAlignmentX(LEFT_ALIGNMENT);
        for (java.awt.Component control : List.of(previous, month, year, next, today, refresh)) {
            controls.add(control);
        }
        header.add(controls);
        header.add(Box.createVerticalStrut(8));
        status.setForeground(MUTED);
        status.setAlignmentX(LEFT_ALIGNMENT);
        header.add(status);

        previous.addActionListener(event -> loadMonth(model.getMonth().minusMonths(1)));
        next.addActionListener(event -> loadMonth(model.getMonth().plusMonths(1)));
        today.addActionListener(event -> loadDate(LocalDate.now(clock)));
        refresh.addActionListener(event -> loadDate(model.getSelectedDate()));
        month.addActionListener(event -> loadFromControls());
        year.addChangeListener(event -> loadFromControls());
        return header;
    }

    private JPanel buildCalendar() {
        final JPanel calendar = new JPanel(new BorderLayout(0, 5));
        calendar.setOpaque(false);
        final JPanel weekdays = new JPanel(new GridLayout(1, DAYS_PER_WEEK, 5, 0));
        weekdays.setOpaque(false);
        for (String day : List.of("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")) {
            final JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setForeground(MUTED);
            weekdays.add(label);
        }
        calendar.add(weekdays, BorderLayout.NORTH);
        grid.setOpaque(false);
        for (int index = 0; index < days.length; index++) {
            final JButton cell = new JButton();
            cell.setHorizontalAlignment(SwingConstants.LEFT);
            cell.setVerticalAlignment(SwingConstants.TOP);
            cell.setContentAreaFilled(false);
            cell.setOpaque(true);
            cell.setForeground(INK);
            cell.addActionListener(event ->
                    controller.selectDate((LocalDate) cell.getClientProperty("date")));
            days[index] = cell;
            grid.add(cell);
        }
        calendar.add(grid, BorderLayout.CENTER);
        return calendar;
    }

    private JPanel buildDetails() {
        final JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 14, 15, 14)));
        panel.setPreferredSize(new Dimension(325, 0));
        dayTitle.setForeground(INK);
        dayTitle.setFont(dayTitle.getFont().deriveFont(Font.BOLD, 15f));
        details.setForeground(INK);
        details.setFixedCellHeight(53);
        details.getAccessibleContext().setAccessibleName("Selected day's deadlines");
        final JScrollPane scroll = new JScrollPane(details);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(dayTitle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadFromControls() {
        if (!updatingControls) {
            loadMonth(YearMonth.of((int) year.getValue(), (Month) month.getSelectedItem()));
        }
    }

    private void loadMonth(YearMonth target) {
        final LocalDate currentDate = LocalDate.now(clock);
        loadDate(target.equals(YearMonth.from(currentDate)) ? currentDate : target.atDay(1));
    }

    private void loadDate(LocalDate date) {
        if (!connected) {
            model.browseDate(date);
        }
        else if (!model.isLoading()) {
            model.beginLoad(date);
            final YearMonth target = YearMonth.from(date);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    controller.loadMonth(target);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    }
                    catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        model.showMonth(target, java.util.Map.of(),
                                "Calendar load was interrupted.");
                    }
                    catch (ExecutionException exception) {
                        model.showMonth(target, java.util.Map.of(),
                                "Could not load the calendar. Try Refresh.");
                    }
                }
            }.execute();
        }
    }

    private void render() {
        final YearMonth displayed = model.getMonth();
        updatingControls = true;
        month.setSelectedItem(displayed.getMonth());
        year.setValue(displayed.getYear());
        updatingControls = false;

        final boolean ready = !model.isLoading();
        previous.setEnabled(ready && !displayed.equals(YearMonth.of(FIRST_YEAR, Month.JANUARY)));
        next.setEnabled(ready && !displayed.equals(YearMonth.of(LAST_YEAR, Month.DECEMBER)));
        for (java.awt.Component control : List.of(month, year, today)) {
            control.setEnabled(ready);
        }
        refresh.setEnabled(ready && connected);
        status.setText(connected ? model.getStatus() : "Connect Quercus to see your deadlines.");
        final int offset = displayed.atDay(1).getDayOfWeek().getValue() - 1;
        for (int index = 0; index < days.length; index++) {
            final int day = index - offset + 1;
            final LocalDate date =
                    day >= 1 && day <= displayed.lengthOfMonth() ? displayed.atDay(day) : null;
            final JButton cell = days[index];
            cell.putClientProperty("date", date);
            cell.setEnabled(date != null);
            cell.setText(date == null ? "" : dayPreview(date));
            cell.getAccessibleContext().setAccessibleName(date == null ? "" : DAY_TITLE.format(date)
                    + ", " + model.getDeadlines(date).size() + " deadlines");
        }
        renderSelection();
    }

    private String dayPreview(LocalDate date) {
        final List<DeadlineSummary> deadlines = model.getDeadlines(date);
        final StringBuilder text =
                new StringBuilder("<html><b>").append(date.getDayOfMonth()).append("</b>");
        final int previewCount = Math.min(MAX_PREVIEWS, deadlines.size());
        for (DeadlineSummary deadline : deadlines.subList(0, previewCount)) {
            final String label = deadline.getCourse() + " · " + deadline.getTitle();
            final String preview = label.length() > 18 ? label.substring(0, 17) + "…" : label;
            text.append("<br>").append(escape(preview));
        }
        if (deadlines.size() > MAX_PREVIEWS) {
            text.append("<br>+").append(deadlines.size() - MAX_PREVIEWS).append(" more");
        }
        return text.append("</html>").toString();
    }

    private void renderSelection() {
        final LocalDate selected = model.getSelectedDate();
        final LocalDate currentDate = LocalDate.now(clock);
        for (JButton cell : days) {
            final Object cellDate = cell.getClientProperty("date");
            final boolean isToday = currentDate.equals(cellDate);
            final boolean isSelected = selected.equals(cellDate);
            cell.setBackground(cellDate == null ? BACKGROUND : isSelected ? SELECTED : WHITE);
            cell.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isToday || isSelected ? ACCENT : BORDER, isToday ? 2 : 1),
                    BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        }

        dayTitle.setText(DAY_TITLE.format(selected));
        final List<DeadlineSummary> selectedDeadlines = model.getDeadlines(selected);
        if (!connected) {
            details.setListData(new String[]{"Your synced deadlines will appear here."});
        }
        else if (selectedDeadlines.isEmpty()) {
            final String message = model.isLoading() ? "Loading…" : "No deadlines for this day";
            details.setListData(new String[]{message});
        }
        else {
            details.setListData(selectedDeadlines.stream()
                    .map(CalendarView::detail)
                    .toArray(String[]::new));
        }
    }

    private static String detail(DeadlineSummary deadline) {
        final String points = deadline.getPointsPossible() == null ? "Points unavailable"
                : deadline.getPointsPossible().stripTrailingZeros().toPlainString() + " pts";
        return "<html><b>" + escape(deadline.getTitle()) + "</b><br>"
                + escape(deadline.getCourse()) + " · " + DUE_TIME.format(deadline.getDueAt())
                + " · " + points + "</html>";
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
