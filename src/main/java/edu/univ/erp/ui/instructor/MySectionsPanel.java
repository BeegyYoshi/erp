package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.InstructorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MySectionsPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int instructorId;

    private JTable table;
    private DefaultTableModel model;
    private List<Map<String, Object>> sections;

    public MySectionsPanel(MainFrame mainFrame, int instructorId) {
        this.mainFrame = mainFrame;
        this.instructorId = instructorId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Sections", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Course", "Day/Time", "Room"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.addActionListener(e -> mainFrame.showScreen("instructor"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        // 🔥 DOUBLE-CLICK event to open the section dashboard
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int sectionId = (int) sections.get(row).get("section_id");

                    openSectionDashboard(sectionId);
                }
            }
        });
    }

    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            sections = InstructorDAO.fetchInstructorSections(instructorId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sections\n" + e.getMessage());
            return;
        }

        for (Map<String, Object> s : sections) {
            model.addRow(new Object[]{
                    s.get("course"),
                    s.get("day_time"),
                    s.get("room")
            });
        }

        revalidate();
        repaint();
    }

    private void openSectionDashboard(int sectionId) {
        SectionDashboardPanel p = new SectionDashboardPanel(mainFrame, instructorId, sectionId);
        mainFrame.loadPanel("section_dash_" + sectionId, p);
        mainFrame.showScreen("section_dash_" + sectionId);
    }
}
