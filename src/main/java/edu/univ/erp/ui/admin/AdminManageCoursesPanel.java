package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AdminCourseDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminManageCoursesPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel model;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminManageCoursesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        loadCourses();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ----- Title -----
        JLabel title = new JLabel("Manage Courses", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        add(title, BorderLayout.NORTH);

        // ----- Table -----
        model = new DefaultTableModel(new String[]{"ID","Code","Title","Credits"}, 0);
        JTable table = new JTable(model);

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

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp, BorderLayout.CENTER);

        // ----- Bottom Buttons -----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottom.setBackground(BG_DARK);

        JButton btnAdd = styledButton("Add");
        JButton btnEdit = styledButton("Edit");
        JButton btnDelete = styledButton("Delete");
        JButton btnBack = styledButton("Back");

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ----- Button Actions -----
        btnBack.addActionListener(e -> mainFrame.showScreen("admin"));

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            showEditDialog(id);
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;

            int id = (int) model.getValueAt(row, 0);

            try {
                boolean ok = AdminCourseDAO.deleteCourse(id);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Failed to delete course");
                }
                loadCourses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    // ---------- Styled Button ----------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    // ---------- Styled Text Field for Dialogs ----------
    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(PANEL_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return tf;
    }

    // ---------- Load Courses ----------
    private void loadCourses() {
        try {
            model.setRowCount(0);
            List<Map<String,Object>> list = AdminCourseDAO.listAllCourses();

            for (Map<String,Object> c : list) {
                model.addRow(new Object[]{
                        c.get("course_id"),
                        c.get("code"),
                        c.get("title"),
                        c.get("credits")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load courses:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------- Add Dialog ----------
    private void showAddDialog() {
        JTextField code = styledTextField();
        JTextField title = styledTextField();
        JTextField credits = styledTextField();

        Object[] fields = {
                styledDialogLabel("Course Code:"), code,
                styledDialogLabel("Course Title:"), title,
                styledDialogLabel("Credits:"), credits
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Add Course", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                AdminCourseDAO.insertCourse(
                        code.getText(),
                        title.getText(),
                        Integer.parseInt(credits.getText())
                );
                loadCourses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ---------- Edit Dialog ----------
    private void showEditDialog(int courseId) {
        JTextField code = styledTextField();
        JTextField title = styledTextField();
        JTextField credits = styledTextField();

        // prefill
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((int) model.getValueAt(i, 0) == courseId) {
                code.setText(model.getValueAt(i, 1).toString());
                title.setText(model.getValueAt(i, 2).toString());
                credits.setText(model.getValueAt(i, 3).toString());
            }
        }

        Object[] fields = {
                styledDialogLabel("Course Code:"), code,
                styledDialogLabel("Course Title:"), title,
                styledDialogLabel("Credits:"), credits
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Edit Course", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                AdminCourseDAO.updateCourse(
                        courseId,
                        code.getText(),
                        title.getText(),
                        Integer.parseInt(credits.getText())
                );
                loadCourses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private JLabel styledDialogLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        return lbl;
    }
}
