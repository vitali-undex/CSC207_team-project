package view;

import interface_adapter.tasks.AddTaskController;
import interface_adapter.tasks.CompleteTaskController;
import interface_adapter.tasks.CourseOption;
import interface_adapter.tasks.DeleteTaskController;
import interface_adapter.tasks.TaskRow;
import interface_adapter.tasks.TasksState;
import interface_adapter.tasks.TasksViewModel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The "My tasks" tab (use case 5). A form for adding a task above a table of
 * every task. Ticking the Done box ticks a task off, and the Delete button
 * deletes the selected task. Overdue tasks are shown in red.
 */
public class TasksView extends JPanel implements PropertyChangeListener {

    private static final Color OVERDUE_COLOR = new Color(190, 30, 30);
    private static final Color DONE_COLOR = Color.GRAY;
    private static final int DONE_COLUMN = 0;
    private static final int TITLE_COLUMN = 1;
    private static final int COURSE_COLUMN = 2;
    private static final int DUE_COLUMN = 3;

    private final TasksViewModel tasksViewModel;

    private final JTextField titleField = new JTextField(30);
    private final JComboBox<CourseOption> courseMenu = new JComboBox<>();
    private final JCheckBox hasDueDateBox = new JCheckBox(TasksViewModel.HAS_DUE_DATE_LABEL);
    private final JSpinner dueDateSpinner =
            new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
    private final JButton addButton = new JButton(TasksViewModel.ADD_BUTTON_LABEL);
    private final JLabel errorLabel = new JLabel(" ");

    private final TaskTableModel tableModel = new TaskTableModel();
    private final JTable table = new JTable(tableModel);
    private final JButton deleteButton = new JButton(TasksViewModel.DELETE_BUTTON_LABEL);

    private AddTaskController addTaskController;
    private CompleteTaskController completeTaskController;
    private DeleteTaskController deleteTaskController;

    /**
     * Creates the tab and starts listening to the view model.
     *
     * @param tasksViewModel the view model this tab shows
     */
    public TasksView(TasksViewModel tasksViewModel) {
        this.tasksViewModel = tasksViewModel;
        tasksViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(createForm(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);

        showState(tasksViewModel.getState());
    }

    public void setAddTaskController(AddTaskController addTaskController) {
        this.addTaskController = addTaskController;
    }

    public void setCompleteTaskController(CompleteTaskController completeTaskController) {
        this.completeTaskController = completeTaskController;
    }

    public void setDeleteTaskController(DeleteTaskController deleteTaskController) {
        this.deleteTaskController = deleteTaskController;
    }

    public String getViewName() {
        return tasksViewModel.getViewName();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final TasksState state = (TasksState) evt.getNewValue();
        showState(state);

        if (TasksViewModel.TASK_ADDED.equals(evt.getPropertyName())) {
            titleField.setText("");
            titleField.requestFocusInWindow();
        }
        else if (TasksViewModel.PAST_DUE_QUESTION.equals(evt.getPropertyName())) {
            askToConfirmPastDueDate(state.getPastDueQuestion());
        }
    }

    private JPanel createForm() {
        final JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.add(new JLabel(TasksViewModel.TITLE_LABEL));
        titleRow.add(titleField);

        final JSpinner.DateEditor dateEditor =
                new JSpinner.DateEditor(dueDateSpinner, "EEE, MMM d, yyyy");
        dateEditor.getFormat().setLenient(false);
        // Keep invalid input visible when Add task receives focus, so it can be rejected.
        dateEditor.getTextField().setFocusLostBehavior(JFormattedTextField.PERSIST);
        dueDateSpinner.setEditor(dateEditor);
        dueDateSpinner.setEnabled(false);
        hasDueDateBox.addActionListener(
                event -> dueDateSpinner.setEnabled(hasDueDateBox.isSelected()));

        final JPanel detailsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        detailsRow.add(new JLabel(TasksViewModel.COURSE_LABEL));
        detailsRow.add(courseMenu);
        detailsRow.add(hasDueDateBox);
        detailsRow.add(dueDateSpinner);
        detailsRow.add(addButton);

        errorLabel.setForeground(OVERDUE_COLOR);
        final JPanel errorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        errorRow.add(errorLabel);

        addButton.addActionListener(event -> addTask(false));
        titleField.addActionListener(event -> addTask(false));

        final JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(titleRow);
        form.add(detailsRow);
        form.add(errorRow);
        return form;
    }

    private JScrollPane createTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(String.class, new TaskCellRenderer());
        table.setPreferredScrollableViewportSize(new Dimension(680, 300));

        table.getColumnModel().getColumn(DONE_COLUMN).setMaxWidth(60);
        table.getColumnModel().getColumn(TITLE_COLUMN).setPreferredWidth(340);
        table.getColumnModel().getColumn(COURSE_COLUMN).setPreferredWidth(110);
        table.getColumnModel().getColumn(DUE_COLUMN).setPreferredWidth(150);

        table.getSelectionModel().addListSelectionListener(
                event -> deleteButton.setEnabled(table.getSelectedRow() >= 0));
        return new JScrollPane(table);
    }

    private JPanel createButtons() {
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(event -> deleteSelectedTask());

        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttons.add(deleteButton);
        return buttons;
    }

    private void showState(TasksState state) {
        showCourseOptions(state.getCourseOptions());

        final String selectedTaskId = selectedTaskId();
        tableModel.setRows(state.getRows());
        selectTask(selectedTaskId);

        if (state.getErrorMessage().isEmpty()) {
            errorLabel.setText(" ");
        }
        else {
            errorLabel.setText(state.getErrorMessage());
        }
    }

    private void showCourseOptions(List<CourseOption> options) {
        final String selectedCourseId = selectedCourseId();
        courseMenu.removeAllItems();
        for (CourseOption option : options) {
            courseMenu.addItem(option);
            if (selectedCourseId != null && selectedCourseId.equals(option.getCourseId())) {
                courseMenu.setSelectedItem(option);
            }
        }
    }

    private void addTask(boolean pastDueConfirmed) {
        try {
            addTaskController.execute(titleField.getText(), selectedCourseId(), selectedDueDate(),
                    pastDueConfirmed);
        }
        catch (ParseException exception) {
            final TasksState state = tasksViewModel.getState();
            state.setErrorMessage(TasksViewModel.INVALID_DUE_DATE_MESSAGE);
            state.setPastDueQuestion("");
            tasksViewModel.setState(state);
            tasksViewModel.firePropertyChanged();
        }
    }

    /**
     * Asks the student whether to add a task whose due date has passed. If
     * they say yes, the same task is sent again with the confirmation set.
     */
    private void askToConfirmPastDueDate(String question) {
        final int answer = JOptionPane.showConfirmDialog(this, question,
                TasksViewModel.PAST_DUE_DIALOG_TITLE, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            addTask(true);
        }
    }

    private void deleteSelectedTask() {
        final String taskId = selectedTaskId();
        if (taskId != null) {
            deleteTaskController.execute(taskId);
        }
    }

    private String selectedCourseId() {
        final CourseOption selected = (CourseOption) courseMenu.getSelectedItem();
        if (selected == null) {
            return null;
        }
        return selected.getCourseId();
    }

    private LocalDate selectedDueDate() throws ParseException {
        if (!hasDueDateBox.isSelected()) {
            return null;
        }
        dueDateSpinner.commitEdit();
        final Date date = (Date) dueDateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String selectedTaskId() {
        final int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return tableModel.getRow(row).getId();
    }

    private void selectTask(String taskId) {
        if (taskId == null) {
            return;
        }
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (taskId.equals(tableModel.getRow(row).getId())) {
                table.setRowSelectionInterval(row, row);
            }
        }
    }

    /** Shows the task rows. Ticking the Done box sends the change to the controller. */
    private class TaskTableModel extends AbstractTableModel {

        private final String[] columnNames = {TasksViewModel.DONE_COLUMN,
            TasksViewModel.TITLE_COLUMN, TasksViewModel.COURSE_COLUMN, TasksViewModel.DUE_COLUMN};
        private List<TaskRow> rows = Collections.emptyList();

        void setRows(List<TaskRow> newRows) {
            rows = new ArrayList<>(newRows);
            fireTableDataChanged();
        }

        TaskRow getRow(int row) {
            return rows.get(row);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            if (column == DONE_COLUMN) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == DONE_COLUMN;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final TaskRow taskRow = rows.get(row);
            switch (column) {
                case DONE_COLUMN:
                    return taskRow.isDone();
                case TITLE_COLUMN:
                    return taskRow.getTitle();
                case COURSE_COLUMN:
                    return taskRow.getCourse();
                default:
                    return taskRow.getDue();
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == DONE_COLUMN) {
                completeTaskController.execute(rows.get(row).getId(), (Boolean) value);
            }
        }
    }

    /** Shows overdue tasks in red and finished tasks in grey. */
    private class TaskCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable taskTable, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            final Component cell = super.getTableCellRendererComponent(taskTable, value,
                    isSelected, hasFocus, row, column);
            if (!isSelected) {
                final TaskRow taskRow = tableModel.getRow(row);
                if (taskRow.isOverdue()) {
                    cell.setForeground(OVERDUE_COLOR);
                }
                else if (taskRow.isDone()) {
                    cell.setForeground(DONE_COLOR);
                }
                else {
                    cell.setForeground(taskTable.getForeground());
                }
            }
            return cell;
        }
    }
}
