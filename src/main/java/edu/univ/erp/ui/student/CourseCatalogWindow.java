package edu.univ.erp.ui.student;

import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CourseCatalogWindow {

    // call CourseCatalogWindow.show(studentId)
    public static void show(int studentId) {
        JFrame frame = new JFrame("Course Catalog");
        frame.setSize(900, 450);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] cols = {"Course ID", "Code", "Title", "Credits", "Section ID", "Instructor", "Day/Time", "Room", "Capacity", "Semester", "Year"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        // Load data (runs on EDT; for big datasets consider SwingWorker)
        try {
            List<Map<String, Object>> rows = StudentService.getCourseCatalog();
            for (Map<String, Object> r : rows) {
                model.addRow(new Object[] {
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
            JOptionPane.showMessageDialog(frame, "Failed to load catalog: " + ex.getMessage());
            ex.printStackTrace();
        }

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(title, BorderLayout.CENTER);

        frame.add(top, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // Optional: small search/filter panel
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        close.addActionListener(e -> frame.dispose());
        bottom.add(close);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
