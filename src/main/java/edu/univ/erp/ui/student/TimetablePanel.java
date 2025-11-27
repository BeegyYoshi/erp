package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TimetablePanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color ACCENT = Color.decode("#39AEA8");
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);

    public TimetablePanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh(); // Initial load
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---------- Title ----------
        JLabel title = new JLabel("Weekly Timetable", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Table Setup ----------
        String[] cols = {"Code", "Title", "Day/Time", "Room"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setForeground(TEXT_LIGHT);
        table.setBackground(PANEL_DARK);
        table.setGridColor(ACCENT.darker());
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Bottom Buttons ----------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton back = styledButton("Back");
        back.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);
    }

    // ---------- Styled Components ----------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
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
