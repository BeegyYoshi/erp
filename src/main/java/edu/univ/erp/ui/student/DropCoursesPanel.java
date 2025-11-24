package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class DropCoursesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    // holds section_id for each row
    private List<Integer> sectionIds = new ArrayList<>();

    public DropCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Drop Enrolled Courses", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Drop", "Course", "Instructor", "Day/Time", "Room"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0; // Only checkbox column editable
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton drop = new JButton("Drop Selected");
        JButton back = new JButton("Back");

        bottom.add(drop);
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);

        drop.addActionListener(e -> dropSelected());
        back.addActionListener(e -> mainFrame.showScreen("student"));
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        sectionIds.clear();

        try (ResultSet rs = EnrollmentDAO.fetchActiveEnrollments(studentId)) {

            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                String code = rs.getString("code");
                String title = rs.getString("title");
                String instructor = rs.getString("instructor");
                String day = rs.getString("day_time");
                String room = rs.getString("room");

                sectionIds.add(sectionId);

                model.addRow(new Object[]{
                        false,                               // checkbox
                        code + " - " + title,
                        instructor,
                        day,
                        room
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load enrolled courses.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropSelected() {
        boolean any = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            boolean selected = (boolean) model.getValueAt(i, 0);

            if (selected) {
                any = true;
                int sectionId = sectionIds.get(i);

                try {
                    EnrollmentDAO.dropEnrollment(studentId, sectionId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Failed to drop section " + sectionId + "\n" + ex.getMessage());
                }
            }
        }

        if (!any) {
            JOptionPane.showMessageDialog(this, "Select at least one course.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Course(s) dropped.");
        refresh();
    }
}
