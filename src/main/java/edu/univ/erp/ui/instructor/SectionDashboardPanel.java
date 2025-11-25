package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.GradeDAO;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

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
    private void exportCSV() {
        try {
            String courseCode = InstructorDAO.getCourseCodeForSection(sectionId);

            // Filename
            String filename = courseCode + "_grades.csv";

            // Fetch data
            List<Map<String, Object>> students = GradeDAO.getStudentsWithGrades(sectionId);
            List<Map<String, Object>> components = GradeDAO.getComponents(sectionId);

            // Build CSV
            StringBuilder sb = new StringBuilder();

            // Header
            sb.append("Student,");
            for (Map<String, Object> c : components) {
                sb.append(c.get("component_name")).append(",");
            }
            sb.append("Final Grade,Letter Grade\n");

            // Rows
            for (Map<String, Object> st : students) {
                String studentName = st.get("student").toString();
                int enrollmentId = (int) st.get("enrollment_id");

                sb.append(studentName).append(",");

                // Component scores
                Map<Integer, Double> scores = GradeDAO.getScoresForStudent(enrollmentId);

                for (Map<String, Object> c : components) {
                    int compId = (int) c.get("component_id");
                    Double score = scores.get(compId);
                    sb.append(score == null ? "" : score).append(",");
                }

                // Final + letter
                Double finalGrade = GradeDAO.getFinalGrade(enrollmentId, sectionId);
                String letter = GradeDAO.getLetterGrade(enrollmentId);

                sb.append(finalGrade == null ? "" : finalGrade).append(",");
                sb.append(letter == null ? "" : letter).append("\n");
            }

            // Save file
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File(filename));

            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.nio.file.Files.writeString(
                        chooser.getSelectedFile().toPath(),
                        sb.toString()
                );
                JOptionPane.showMessageDialog(this, "CSV saved successfully!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV:\n" + ex.getMessage());
            ex.printStackTrace();
        }
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
        JButton downloadCsvBtn = new JButton("Download Grades CSV");

        center.add(assessmentBtn);
        center.add(gradeEntryBtn);
        center.add(statsBtn);
        center.add(downloadCsvBtn);

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
            GradeEntryPanel p = new GradeEntryPanel(mainFrame, sectionId);
            mainFrame.loadPanel("gradeEntry_" + sectionId, p);
            mainFrame.showScreen("gradeEntry_" + sectionId);
        });

        // Open stats panel (if implemented)
        /*
        statsBtn.addActionListener(e -> {
            var panel = new ClassStatisticsPanel(mainFrame, sectionId);
            mainFrame.loadPanel("stats_" + sectionId, panel);
            mainFrame.showScreen("stats_" + sectionId);
        });
         */

        statsBtn.addActionListener(e -> {
            ClassStatisticsPanel p = new ClassStatisticsPanel(mainFrame, sectionId);
            mainFrame.loadPanel("classStats_" + sectionId, p);
            mainFrame.showScreen("classStats_" + sectionId);
        });

        downloadCsvBtn.addActionListener(e -> {
            try {
                // 1. Get course code for file name
                String courseCode = GradeDAO.getCourseCodeForSection(sectionId);
                if (courseCode == null || courseCode.isEmpty()) {
                    courseCode = "section_" + sectionId;
                }

                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new java.io.File(courseCode + "_grades.csv"));

                int choice = fc.showSaveDialog(this);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fc.getSelectedFile();

                    // 2. Export CSV using GradeDAO
                    GradeDAO.exportSectionGradesToCSV(sectionId, file.getAbsolutePath());

                    JOptionPane.showMessageDialog(this,
                            "CSV exported successfully!\nSaved as: " + file.getName());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV:\n" + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> mainFrame.showScreen("instructor_sections"));
    }
}
