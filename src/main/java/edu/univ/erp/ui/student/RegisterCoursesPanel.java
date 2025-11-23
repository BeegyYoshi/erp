package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RegisterCoursesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;
    private List<Map<String, Object>> sections;

    public RegisterCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Available Course Sections");
        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(heading, BorderLayout.NORTH);

        // Table Columns with Checkbox
        String[] cols = {"Select", "Course", "Instructor", "Capacity", "Day/Time"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox column editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton register = new JButton("Register");
        JButton back = new JButton("Back");

        register.addActionListener(e -> registerSelected());
        back.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(register);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            sections = SectionDAO.fetchAvailableSections(studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sections:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Populate table with checkbox + section info
        for (Map<String, Object> s : sections) {
            model.addRow(new Object[]{
                    false,
                    s.get("title") + " (" + s.get("code") + ")",
                    s.get("instructor"),
                    s.get("capacity").toString(),
                    s.get("day_time")
            });
        }

        revalidate();
        repaint();
    }

    private void registerSelected() {
        boolean any = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            boolean selected = (boolean) model.getValueAt(i, 0);

            if (selected) {
                any = true;
                int sectionId = (int) sections.get(i).get("section_id");

                try {
                    EnrollmentDAO.enrollStudent(studentId, sectionId);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Failed to register:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (!any) {
            JOptionPane.showMessageDialog(this, "Please select at least one course to register.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration complete!");
        refresh();
    }
}
