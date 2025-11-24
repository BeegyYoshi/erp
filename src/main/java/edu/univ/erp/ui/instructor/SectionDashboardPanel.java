package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.InstructorDAO;

import javax.swing.*;
import java.awt.*;

public class SectionDashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final int instructorId;
    private final int sectionId;

    private String courseCode = "Course";

    public SectionDashboardPanel(MainFrame mainFrame, int instructorId, int sectionId) {
        this.mainFrame = mainFrame;
        this.instructorId = instructorId;
        this.sectionId = sectionId;

        loadCourseCode();
        buildUI();
    }

    private void loadCourseCode() {
        try {
            courseCode = InstructorDAO.getCourseCodeForSection(sectionId);
        } catch (Exception e) {
            courseCode = "UNKNOWN";
            JOptionPane.showMessageDialog(this,
                    "Failed to load course code for section " + sectionId,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel(courseCode + " Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton assessmentBtn = new JButton("Assessment Editor");
        JButton gradeEntryBtn = new JButton("Grade Entry");
        JButton statsBtn = new JButton("Class Statistics");

        center.add(assessmentBtn);
        center.add(gradeEntryBtn);
        center.add(statsBtn);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back");
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // Open assessment panel
        assessmentBtn.addActionListener(e -> {
            var p = new AssessmentEditorPanel(mainFrame, sectionId);
            mainFrame.loadPanel("assessmentEditor_" + sectionId, p);
            mainFrame.showScreen("assessmentEditor_" + sectionId);
        });

        // Open grade entry (if implemented)
        gradeEntryBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Grade Entry Panel Not Implemented Yet");
        });

        // Open stats panel (if implemented)
        statsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Class Statistics Panel Not Implemented Yet");
        });

        backBtn.addActionListener(e -> mainFrame.showScreen("instructor_sections"));
    }
}
