package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class DropCoursesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JPanel listPanel;
    private List<JCheckBox> checkboxes;
    private List<Map<String, Object>> enrolledCourses;

    public DropCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Drop Courses", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton dropBtn = new JButton("Drop Selected");
        JButton backBtn = new JButton("Back");

        bottom.add(dropBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        dropBtn.addActionListener(e -> dropSelectedCourses());
        backBtn.addActionListener(e -> mainFrame.showScreen("student"));
    }

    @Override
    public void refresh() {
        listPanel.removeAll();
        checkboxes = new ArrayList<>();
        enrolledCourses = new ArrayList<>();

        try (ResultSet rs = EnrollmentDAO.fetchActiveEnrollments(studentId)) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("section_id", rs.getInt("section_id"));
                map.put("code", rs.getString("code"));
                map.put("title", rs.getString("title"));

                enrolledCourses.add(map);

                String label = rs.getString("code") +
                        " - " + rs.getString("title") +
                        " | Section: " + rs.getInt("section_id");

                JCheckBox cb = new JCheckBox(label);
                checkboxes.add(cb);

                listPanel.add(cb);
                listPanel.add(Box.createVerticalStrut(4));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load enrolled courses.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        revalidate();
        repaint();
    }

    private void dropSelectedCourses() {
        boolean anySelected = false;

        for (int i = 0; i < checkboxes.size(); i++) {
            JCheckBox box = checkboxes.get(i);
            if (box.isSelected()) {
                anySelected = true;

                int sectionId = (int) enrolledCourses.get(i).get("section_id");

                try {
                    boolean ok = EnrollmentDAO.dropEnrollment(studentId, sectionId);

                    if (!ok) {
                        JOptionPane.showMessageDialog(this,
                                "Could not drop Section " + sectionId,
                                "Drop Failed",
                                JOptionPane.WARNING_MESSAGE);
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error dropping Section " + sectionId +
                                    "\n" + ex.getMessage(),
                            "SQL Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        if (!anySelected) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one course to drop.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Course(s) dropped successfully!");

        refresh(); // reload UI after drop
    }
}
