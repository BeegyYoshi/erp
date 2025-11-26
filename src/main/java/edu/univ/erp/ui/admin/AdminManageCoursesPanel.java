package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AdminCourseDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminManageCoursesPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel model;

    public AdminManageCoursesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        loadCourses();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Courses", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID","Code","Title","Credits"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnBack = new JButton("Back");

        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

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
                ex.printStackTrace();
            }
        });
    }

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
            ex.printStackTrace();
        }
    }

    private void showAddDialog() {
        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField();

        Object[] fields = {
                "Course Code:", code,
                "Course Title:", title,
                "Credits:", credits
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

    private void showEditDialog(int courseId) {
        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField();

        // prefill from table
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((int) model.getValueAt(i, 0) == courseId) {
                code.setText(model.getValueAt(i, 1).toString());
                title.setText(model.getValueAt(i, 2).toString());
                credits.setText(model.getValueAt(i, 3).toString());
            }
        }

        Object[] fields = {
                "Course Code:", code,
                "Course Title:", title,
                "Credits:", credits
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
}
