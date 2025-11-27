package edu.univ.erp.ui.student;

import edu.univ.erp.service.StudentService;
import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CourseCatalogPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color ACCENT = Color.decode("#39AEA8");
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);

    public CourseCatalogPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh(); // load initial data
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Table Columns ----
        String[] cols = {
                "Course ID", "Code", "Title", "Credits",
                "Section ID", "Instructor", "Day/Time",
                "Room", "Capacity", "Semester", "Year"
        };

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // ---- Table Setup ----
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton backBtn = styledButton("Back");
        backBtn.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // ---- Styled Components ----
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
                    "Failed to load catalog:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }

        revalidate();
        repaint();
    }
}
