package edu.univ.erp.ui.student;

import edu.univ.erp.service.StudentService;
import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CourseCatalogPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    public CourseCatalogPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh(); // load initial data
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Columns
        String[] cols = {
                "Course ID", "Code", "Title", "Credits",
                "Section ID", "Instructor", "Day/Time",
                "Room", "Capacity", "Semester", "Year"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        // Clear table
        model.setRowCount(0);

        try {
            List<Map<String, Object>> rows = StudentService.getCourseCatalog();
            for (Map<String, Object> r : rows) {
                model.addRow(new Object[]{
                        r.get("course_id"),
                        r.get("code"),
                        r.get("title"),
                        r.get("credits"),
                        r.get("section_id"),
                        r.get("instructor_name"),
                        r.get("day_time"),
                        r.get("room"),
                        r.get("capacity"),
                        r.get("semester"),
                        r.get("year")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load catalog: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

        revalidate();
        repaint();
    }
}
