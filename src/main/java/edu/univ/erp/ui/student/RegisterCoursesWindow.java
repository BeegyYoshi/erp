package edu.univ.erp.ui.student;

import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class RegisterCoursesWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("Register for Courses");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel heading = new JLabel("Select the sections you want to register:");
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(heading, BorderLayout.NORTH);

        //-------------------------------------------------------
        // Fetch sections from DB
        //-------------------------------------------------------
        List<Map<String, Object>> sections;
        try {
            sections = SectionDAO.fetchAvailableSections(studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load sections.");
            frame.dispose();
            return;
        }

        //-------------------------------------------------------
        // Panel for checkboxes
        //-------------------------------------------------------
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // Keep references to checkboxes for reading later
        List<JCheckBox> checkboxes = new ArrayList<>();

        for (Map<String, Object> s : sections) {
            int sectionId = (int) s.get("section_id");
            String label =
                    s.get("code") + " - " + s.get("title")
                            + " | Section: " + sectionId
                            + " | " + s.get("day_time")
                            + " | Room: " + s.get("room");

            JCheckBox box = new JCheckBox(label);
            checkboxes.add(box);
            listPanel.add(box);

            // Add spacing
            listPanel.add(Box.createVerticalStrut(5));
        }

        //-------------------------------------------------------
        // Scroll pane for list
        //-------------------------------------------------------
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);

        //-------------------------------------------------------
        // Register button
        //-------------------------------------------------------
        JButton registerBtn = new JButton("Register Selected");
        frame.add(registerBtn, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> {

            boolean anySelected = false;

            for (int i = 0; i < checkboxes.size(); i++) {
                if (checkboxes.get(i).isSelected()) {
                    anySelected = true;
                    int sectionId = (int) sections.get(i).get("section_id");

                    try {
                        boolean ok = EnrollmentDAO.enrollStudent(studentId, sectionId);
                        if (!ok) {
                            JOptionPane.showMessageDialog(frame,
                                    "Already enrolled in section " + sectionId);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(frame,
                                "Error enrolling in section " + sectionId + ": " + ex.getMessage());
                    }
                }
            }

            if (!anySelected) {
                JOptionPane.showMessageDialog(frame, "Please select at least one section.");
                return;
            }

            JOptionPane.showMessageDialog(frame, "Registration complete.");
        });

        frame.setVisible(true);
    }
}
