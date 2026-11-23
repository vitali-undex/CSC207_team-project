package view;

import interface_adapter.read_announcements.AnnouncementsViewModel;
import interface_adapter.read_announcements.ReadAnnouncementsController;
import use_case.read_announcements.AnnouncementSummary;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** The Announcements tab: a list of announcements with the selected one's full text. */
public final class AnnouncementsView extends JPanel {
    private static final DateTimeFormatter POSTED =
            DateTimeFormatter.ofPattern("MMM d, h:mm a").withZone(ZoneId.systemDefault());

    private final transient ReadAnnouncementsController controller;
    private final AnnouncementsViewModel viewModel;
    private final DefaultListModel<AnnouncementSummary> listModel = new DefaultListModel<>();
    private final JList<AnnouncementSummary> list = new JList<>(listModel);
    private final JTextArea body = new JTextArea();

    /** Builds the view and wires it to the controller and view model. */
    public AnnouncementsView(final ReadAnnouncementsController controller,
                             final AnnouncementsViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setLayout(new BorderLayout());
        buildComponents();

        viewModel.addPropertyChangeListener(event -> refreshFromViewModel());
    }

    private void buildComponents() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new AnnouncementCellRenderer());
        list.addListSelectionListener(event -> showSelectedBody());

        body.setEditable(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setMargin(new java.awt.Insets(8, 8, 8, 8));

        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(list), new JScrollPane(body));
        split.setDividerLocation(260);

        final JButton refresh = new JButton("Refresh");
        refresh.addActionListener(event -> controller.load());

        add(refresh, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void refreshFromViewModel() {
        if (!viewModel.getError().isEmpty()) {
            body.setText(viewModel.getError());
            listModel.clear();
            return;
        }
        listModel.clear();
        for (final AnnouncementSummary summary : viewModel.getAnnouncements()) {
            listModel.addElement(summary);
        }
        if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
    }

    private void showSelectedBody() {
        final AnnouncementSummary selected = list.getSelectedValue();
        if (selected != null) {
            body.setText(selected.getCourse() + " — " + selected.getTitle() + "\n"
                    + POSTED.format(selected.getPostedAt()) + "\n\n" + selected.getMessage());
            body.setCaretPosition(0);
        }
    }

    /** Renders each row as "[NEW] COURSE — title (posted date)". */
    private static final class AnnouncementCellRenderer
            extends javax.swing.DefaultListCellRenderer {
        @Override
        public java.awt.Component getListCellRendererComponent(
                final JList<?> list, final Object value, final int index,
                final boolean isSelected, final boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            final AnnouncementSummary summary = (AnnouncementSummary) value;
            final String tag = summary.isUnread() ? "[NEW] " : "";
            setText(tag + summary.getCourse() + " — " + summary.getTitle()
                    + "  (" + POSTED.format(summary.getPostedAt()) + ")");
            return this;
        }
    }
}
