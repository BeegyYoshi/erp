package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DropCoursesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    private List<Integer> sectionIds = new ArrayList<>();

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public DropCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Drop Enrolled Courses", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Columns ----
        String[] cols = {"Drop", "Course", "Instructor", "Day/Time", "Room"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 0; // Checkbox only
            }
            @Override public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
        };

        // ---- Table Styling ----
        table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(PANEL_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setGridColor(ACCENT.darker());

        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // ---- Bottom Bar ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton drop = styledButton("Drop Selected");
        JButton back = styledButton("Back");

        drop.addActionListener(e -> dropSelected());
        back.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(drop);
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);
    }

    // ---------- Styled Button ----------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }

    // ---------- Refresh Table ----------
    @Override
    public void refresh() {
        model.setRowCount(0);
        sectionIds.clear();

        try (ResultSet rs = EnrollmentDAO.fetchActiveEnrollments(studentId)) {

            while (rs.next()) {
                int sectionId = rs.getInt("section_id");

                sectionIds.add(sectionId);

                model.addRow(new Object[]{
                        false,
                        rs.getString("code") + " - " + rs.getString("title"),
                        rs.getString("instructor"),
                        rs.getString("day_time"),
                        rs.getString("room")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load enrolled courses.\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        revalidate();
        repaint();
    }

    // ---------- Drop Operation ----------
    private void dropSelected() {

        // Maintenance check
        try {
            if (SettingsDAO.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Maintenance mode is ON.\nDropping courses is disabled.",
                        "Blocked",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to check maintenance mode.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean any = false;

        for (int i = 0; i < model.getRowCount(); i++) {

            boolean selected = (boolean) model.getValueAt(i, 0);

            if (selected) {
                any = true;

                int sectionId = sectionIds.get(i);

                try {
                    EnrollmentDAO.dropEnrollment(studentId, sectionId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to drop section " + sectionId + "\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        if (!any) {
            JOptionPane.showMessageDialog(this, "Select at least one course.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Course(s) dropped successfully.");
        refresh();
    }
}
