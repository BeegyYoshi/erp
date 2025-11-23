package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TimetablePanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    public TimetablePanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh(); // initial load
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Weekly Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Removed Section ID column
        String[] cols = {"Code", "Title", "Day/Time", "Room"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainFrame.showScreen("student"));
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void refresh() {
        model.setRowCount(0); // clear table

        try {
            List<Map<String, Object>> rows = EnrollmentDAO.fetchTimetable(studentId);

            for (Map<String, Object> r : rows) {
                model.addRow(new Object[]{
                        r.get("code"),
                        r.get("title"),
                        r.get("day_time"),
                        r.get("room")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load timetable:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        revalidate();
        repaint();
    }
}
