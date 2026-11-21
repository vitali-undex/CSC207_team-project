package view;

import interface_adapter.calculate_grade.Choice;
import interface_adapter.calculate_grade.GradeCalculatorController;
import interface_adapter.calculate_grade.GradeCalculatorState;
import interface_adapter.calculate_grade.GradeCalculatorViewModel;
import interface_adapter.calculate_grade.GroupRowState;
import interface_adapter.calculate_grade.WeightEntry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The grade calculator screen: pick a course, copy each assignment group's
 * weight from the syllabus, and see the current grade.
 */
public class GradeCalculatorView extends JPanel implements PropertyChangeListener {

    static final int GROUP_COLUMN = 0;
    static final int WEIGHT_COLUMN = 1;
    static final int DROP_COLUMN = 2;
    static final int AVERAGING_COLUMN = 3;
    static final int AVERAGE_COLUMN = 4;
    static final int GRADED_COLUMN = 5;

    private static final String[] COLUMN_NAMES =
        {"Group", "Weight (%)", "Drop lowest", "Items count", "Average", "Graded"};
    private static final int[] COLUMN_WIDTHS = {220, 90, 90, 110, 90, 80};
    private static final Color MUTED = new Color(0x495E76);
    private static final Color ERROR = new Color(0xB00020);
    private static final float TITLE_SIZE = 20f;
    private static final float GRADE_SIZE = 22f;
    private static final int GAP = 8;
    private static final int BORDER = 20;
    private static final int ROW_HEIGHT = 26;
    private static final Dimension TABLE_SIZE = new Dimension(760, 190);

    private final GradeCalculatorViewModel viewModel;
    private final GradeCalculatorController controller;
    private final JComboBox<Choice> courseBox = new JComboBox<>();
    private final JComboBox<Choice> averagingBox = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == WEIGHT_COLUMN || column == DROP_COLUMN || column == AVERAGING_COLUMN;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JButton calculateButton = new JButton("Calculate grade");
    private final JLabel gradeLabel = new JLabel(" ");
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel errorLabel = new JLabel(" ");
    private final List<String> rowGroupIds = new ArrayList<>();
    private boolean updating;

    /**
     * Creates the screen. Call {@link #open()} to load the courses.
     *
     * @param viewModel  the view model this screen shows
     * @param controller receives what the student does
     */
    public GradeCalculatorView(GradeCalculatorViewModel viewModel,
                               GradeCalculatorController controller) {
        super(new BorderLayout(0, GAP * 2));
        this.viewModel = viewModel;
        this.controller = controller;
        viewModel.addPropertyChangeListener(this);

        setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildResults(), BorderLayout.SOUTH);

        courseBox.addActionListener(event -> courseChosen());
        calculateButton.addActionListener(event -> calculate());
        calculateButton.setEnabled(false);
    }

    /**
     * Loads the courses and shows the first one.
     */
    public void open() {
        controller.loadCourses();
        final List<Choice> courses = viewModel.getState().getCourses();
        if (!courses.isEmpty()) {
            controller.selectCourse(courses.get(0).getId());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        final GradeCalculatorState state = viewModel.getState();
        if (GradeCalculatorViewModel.COURSES_PROPERTY.equals(event.getPropertyName())) {
            showCourses(state);
        }
        else if (GradeCalculatorViewModel.ERROR_PROPERTY.equals(event.getPropertyName())) {
            showMessages(state);
        }
        else {
            showBreakdown(state);
            showMessages(state);
        }
    }

    private JComponent buildHeader() {
        final JLabel title = new JLabel("Grade calculator");
        title.setFont(title.getFont().deriveFont(Font.BOLD, TITLE_SIZE));

        final JLabel courseLabel = new JLabel("Course:");
        courseLabel.setLabelFor(courseBox);
        courseBox.getAccessibleContext().setAccessibleName("Course");
        final JPanel courseRow = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        courseRow.add(courseLabel);
        courseRow.add(Box.createHorizontalStrut(GAP));
        courseRow.add(courseBox);

        final JLabel hint = new JLabel("<html>Copy each group's weight from the syllabus. "
                + "Drops remove the lowest marks first.<br>Items count <b>by points</b> "
                + "(bigger assignments count more) or <b>equally</b> "
                + "(each one counts the same).</html>");
        hint.setForeground(MUTED);

        return stack(title, courseRow, hint);
    }

    private JComponent buildTable() {
        table.setRowHeight(ROW_HEIGHT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(TABLE_SIZE);
        table.getTableHeader().setReorderingAllowed(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.getColumnModel().getColumn(AVERAGING_COLUMN)
                .setCellEditor(new DefaultCellEditor(averagingBox));
        for (int column = 0; column < COLUMN_WIDTHS.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(COLUMN_WIDTHS[column]);
        }
        table.getAccessibleContext().setAccessibleName("Assignment groups");
        return new JScrollPane(table);
    }

    private JComponent buildResults() {
        gradeLabel.setFont(gradeLabel.getFont().deriveFont(Font.BOLD, GRADE_SIZE));
        detailLabel.setForeground(MUTED);
        errorLabel.setForeground(ERROR);
        return stack(calculateButton, errorLabel, gradeLabel, detailLabel);
    }

    private static JComponent stack(JComponent... components) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (JComponent component : components) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(component);
            panel.add(Box.createVerticalStrut(GAP));
        }
        return panel;
    }

    private void courseChosen() {
        final Choice course = (Choice) courseBox.getSelectedItem();
        if (!updating && course != null) {
            controller.selectCourse(course.getId());
        }
    }

    private void calculate() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        final List<WeightEntry> entries = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            final Choice averaging = (Choice) tableModel.getValueAt(row, AVERAGING_COLUMN);
            entries.add(new WeightEntry(rowGroupIds.get(row), cellText(row, WEIGHT_COLUMN),
                    cellText(row, DROP_COLUMN), averaging.getId()));
        }
        controller.calculate(viewModel.getState().getCourseId(), entries);
    }

    private String cellText(int row, int column) {
        return Objects.toString(tableModel.getValueAt(row, column), "");
    }

    private void showCourses(GradeCalculatorState state) {
        updating = true;
        courseBox.setModel(new DefaultComboBoxModel<>(state.getCourses().toArray(new Choice[0])));
        updating = false;
        showMessages(state);
    }

    private void showBreakdown(GradeCalculatorState state) {
        if (table.isEditing()) {
            table.getCellEditor().cancelCellEditing();
        }
        updating = true;
        for (int index = 0; index < courseBox.getItemCount(); index++) {
            if (courseBox.getItemAt(index).getId().equals(state.getCourseId())) {
                courseBox.setSelectedIndex(index);
            }
        }
        averagingBox.setModel(
                new DefaultComboBoxModel<>(state.getAveragingChoices().toArray(new Choice[0])));
        updating = false;

        tableModel.setRowCount(0);
        rowGroupIds.clear();
        for (GroupRowState row : state.getRows()) {
            rowGroupIds.add(row.getGroupId());
            tableModel.addRow(new Object[] {row.getName(), row.getWeightText(), row.getDropText(),
                row.getAveraging(), row.getAverageText(), row.getGradedText()});
        }
        calculateButton.setEnabled(!state.getRows().isEmpty());
    }

    private void showMessages(GradeCalculatorState state) {
        gradeLabel.setText(orSpace(state.getGradeText()));
        detailLabel.setText(orSpace(state.getDetailText()));
        errorLabel.setText(orSpace(state.getErrorMessage()));
    }

    /**
     * Returns the text, or a space if it is empty, so labels keep their height.
     */
    private static String orSpace(String text) {
        if (text.isEmpty()) {
            return " ";
        }
        return text;
    }

    JComboBox<Choice> getCourseBox() {
        return courseBox;
    }

    JTable getTable() {
        return table;
    }

    JButton getCalculateButton() {
        return calculateButton;
    }

    JLabel getGradeLabel() {
        return gradeLabel;
    }

    JLabel getDetailLabel() {
        return detailLabel;
    }

    JLabel getErrorLabel() {
        return errorLabel;
    }
}
