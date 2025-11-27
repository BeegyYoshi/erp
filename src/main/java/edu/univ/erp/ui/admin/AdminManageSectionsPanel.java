package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AdminSectionDAO;
import edu.univ.erp.data.AdminCourseDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class AdminManageSectionsPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel model;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminManageSectionsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        loadSections();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Manage Sections", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ---- Table ----
        model = new DefaultTableModel(new String[]{
                "ID","Course","Instructor","Day/Time","Room","Capacity","Semester","Year"
        }, 0);

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
        add(sp, BorderLayout.CENTER);

        // ---- Bottom Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        bottom.setBackground(BG_DARK);

        JButton btnAdd = styledButton("Add Section");
        JButton btnAssign = styledButton("Assign Instructor");
        JButton btnDelete = styledButton("Delete");
        JButton btnBack = styledButton("Back");

        bottom.add(btnAdd);
        bottom.add(btnAssign);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ---- Actions ----
        btnBack.addActionListener(e -> mainFrame.showScreen("admin"));

        btnAdd.addActionListener(e -> addSection());
        btnAssign.addActionListener(e -> assignInstructor(getSelectedSectionId(table)));
        btnDelete.addActionListener(e -> deleteSection(getSelectedSectionId(table)));
    }

    // ---------------- BUTTON STYLE ----------------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    // ---------------- TEXT FIELD STYLE ----------------
    private JTextField styledField() {
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

    private JLabel styledDialogLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        return lbl;
    }

    // ---------------- LOAD SECTIONS ----------------
    private void loadSections() {
        try {
            model.setRowCount(0);
            List<Map<String,Object>> list = AdminSectionDAO.listAllSections();

            for (Map<String,Object> s : list) {
                model.addRow(new Object[]{
                        s.get("section_id"),
                        s.get("code") + " - " + s.get("title"),
                        s.get("instructor") == null ? "None" : s.get("instructor"),
                        s.get("day_time"),
                        s.get("room"),
                        s.get("capacity"),
                        s.get("semester"),
                        s.get("year")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sections:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- GET SELECTED ID ----------------
    private int getSelectedSectionId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        return (int) model.getValueAt(row, 0);
    }

    // ---------------- ADD SECTION ----------------
    private void addSection() {
        try {
            // ----- Load Courses -----
            List<Map<String,Object>> courses = AdminCourseDAO.listAllCourses();
            JComboBox<String> courseBox = new JComboBox<>();

            for (var c : courses)
                courseBox.addItem(c.get("course_id") + " - " + c.get("code"));

            courseBox.setFont(new Font("SansSerif", Font.PLAIN, 15));

            // ----- Load Instructors -----
            JComboBox<String> instructorBox = new JComboBox<>();
            instructorBox.addItem("None");

            Connection conn = edu.univ.erp.data.AuthDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, username FROM users_auth WHERE role='instructor'"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                instructorBox.addItem(rs.getInt("user_id") + " - " + rs.getString("username"));
            }

            instructorBox.setFont(new Font("SansSerif", Font.PLAIN, 15));

            JTextField day = styledField();
            JTextField room = styledField();
            JTextField cap = styledField();
            JTextField sem = styledField();
            JTextField year = styledField();

            Object[] fields = {
                    styledDialogLabel("Course:"), courseBox,
                    styledDialogLabel("Instructor:"), instructorBox,
                    styledDialogLabel("Day/Time:"), day,
                    styledDialogLabel("Room:"), room,
                    styledDialogLabel("Capacity:"), cap,
                    styledDialogLabel("Semester:"), sem,
                    styledDialogLabel("Year:"), year
            };

            int ok = JOptionPane.showConfirmDialog(
                    this, fields, "Add Section", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                int courseId = Integer.parseInt(courseBox.getSelectedItem().toString().split(" - ")[0]);

                String insSel = instructorBox.getSelectedItem().toString();
                Integer instructorId = insSel.equals("None") ? null :
                        Integer.parseInt(insSel.split(" - ")[0]);

                AdminSectionDAO.insertSection(
                        courseId,
                        instructorId,
                        day.getText(),
                        room.getText(),
                        Integer.parseInt(cap.getText()),
                        sem.getText(),
                        Integer.parseInt(year.getText())
                );

                loadSections();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding section:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- ASSIGN INSTRUCTOR ----------------
    private void assignInstructor(int sectionId) {
        if (sectionId < 0) return;

        try {
            JComboBox<String> insBox = new JComboBox<>();
            insBox.setFont(new Font("SansSerif", Font.PLAIN, 15));

            Connection conn = edu.univ.erp.data.AuthDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, username FROM users_auth WHERE role='instructor'"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                insBox.addItem(rs.getInt("user_id") + " - " + rs.getString("username"));
            }

            int ok = JOptionPane.showConfirmDialog(
                    this, new Object[]{styledDialogLabel("Assign Instructor:"), insBox},
                    "Assign Instructor", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                int instructorId = Integer.parseInt(insBox.getSelectedItem().toString().split(" - ")[0]);
                AdminSectionDAO.assignInstructor(sectionId, instructorId);
                loadSections();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed assigning instructor:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- DELETE SECTION ----------------
    private void deleteSection(int sectionId) {
        if (sectionId < 0) return;

        try {
            boolean ok = AdminSectionDAO.deleteSection(sectionId);

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete section with enrolled students.",
                        "Delete Blocked",
                        JOptionPane.WARNING_MESSAGE);
            }

            loadSections();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete section:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
