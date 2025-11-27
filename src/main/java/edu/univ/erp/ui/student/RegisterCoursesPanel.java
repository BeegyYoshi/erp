package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public RegisterCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Heading ----
        JLabel heading = new JLabel("Available Course Sections", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 28));
        heading.setForeground(ACCENT);
        heading.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(heading, BorderLayout.NORTH);

        // ---- Table Columns ----
        String[] cols = {"Select", "Course", "Instructor", "Capacity", "Day/Time"};

        model = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };

        // ---- Table ----
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

        // ---- Bottom Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton register = styledButton("Register");
        JButton back = styledButton("Back");

        register.addActionListener(e -> registerSelected());
        back.addActionListener(e -> mainFrame.showScreen("student"));

        bottom.add(register);
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);
    }

    // ---- Styled Button ----
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

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

    // ---- Refresh Table ----
    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            sections = SectionDAO.fetchAvailableSections(studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load sections:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

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

    // ---- Register Action ----
    private void registerSelected() {

        // Maintenance check
        try {
            if (SettingsDAO.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Maintenance mode is ON.\nYou cannot register right now.",
                        "Maintenance Active",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not verify maintenance mode.\nRegistration halted.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean any = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            boolean selected = (Boolean) model.getValueAt(i, 0);

            if (selected) {
                any = true;

                int sectionId = (int) sections.get(i).get("section_id");

                try {
                    EnrollmentDAO.enrollStudent(studentId, sectionId);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to register:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }

        if (!any) {
            JOptionPane.showMessageDialog(this, "Select at least one course to register.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration successful!");
        refresh();
    }
}
