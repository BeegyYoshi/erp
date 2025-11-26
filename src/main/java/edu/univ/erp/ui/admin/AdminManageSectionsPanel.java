package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AdminSectionDAO;
import edu.univ.erp.data.AdminCourseDAO;
import edu.univ.erp.data.ERPDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminManageSectionsPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel model;

    public AdminManageSectionsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        loadSections();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Sections", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "ID","Course","Instructor","Day/Time","Room","Capacity","Semester","Year"
        }, 0);

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());

        JButton btnAdd = new JButton("Add Section");
        JButton btnAssign = new JButton("Assign Instructor");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack = new JButton("Back");

        bottom.add(btnAdd);
        bottom.add(btnAssign);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> mainFrame.showScreen("admin"));

        btnAdd.addActionListener(e -> addSection());
        btnAssign.addActionListener(e -> assignInstructor(getSelectedSectionId(table)));
        btnDelete.addActionListener(e -> deleteSection(getSelectedSectionId(table)));
    }

    private int getSelectedSectionId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) return -1;
        return (int) model.getValueAt(row, 0);
    }

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
            ex.printStackTrace();
        }
    }

    private void addSection() {
        try {
            // Load courses
            List<Map<String,Object>> courses = AdminCourseDAO.listAllCourses();
            JComboBox<String> courseBox = new JComboBox<>();
            for (var c : courses)
                courseBox.addItem(c.get("course_id") + " - " + c.get("code"));

            // Load instructors
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

            JTextField day = new JTextField();
            JTextField room = new JTextField();
            JTextField cap = new JTextField();
            JTextField sem = new JTextField();
            JTextField year = new JTextField();

            Object[] fields = {
                    "Course:", courseBox,
                    "Instructor:", instructorBox,
                    "Day/Time:", day,
                    "Room:", room,
                    "Capacity:", cap,
                    "Semester:", sem,
                    "Year:", year
            };

            int ok = JOptionPane.showConfirmDialog(this, fields, "Add Section", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                int courseId = Integer.parseInt(courseBox.getSelectedItem().toString().split(" - ")[0]);

                String insSel = instructorBox.getSelectedItem().toString();
                Integer instructorId = insSel.equals("None")
                        ? null
                        : Integer.parseInt(insSel.split(" - ")[0]);

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
            ex.printStackTrace();
        }
    }

    private void assignInstructor(int sectionId) {
        if (sectionId < 0) return;

        try {
            JComboBox<String> insBox = new JComboBox<>();

            Connection conn = edu.univ.erp.data.AuthDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT user_id, username FROM users_auth WHERE role='instructor'"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                insBox.addItem(rs.getInt("user_id") + " - " + rs.getString("username"));
            }

            int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Assign instructor:", insBox},
                    "Assign Instructor", JOptionPane.OK_CANCEL_OPTION);

            if (ok == JOptionPane.OK_OPTION) {
                int instructorId = Integer.parseInt(insBox.getSelectedItem().toString().split(" - ")[0]);
                AdminSectionDAO.assignInstructor(sectionId, instructorId);
                loadSections();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSection(int sectionId) {
        if (sectionId < 0) return;

        try {
            boolean ok = AdminSectionDAO.deleteSection(sectionId);

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete section with enrolled students.");
            }

            loadSections();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
