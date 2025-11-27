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

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

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
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load course code for section " + sectionId,
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel(courseCode + " Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ---- Center Buttons ----
        JPanel center = new JPanel(new GridLayout(4, 1, 15, 15));
        center.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        center.setBackground(BG_DARK);

        JButton assessmentBtn = styledButton("Assessment Editor");
        JButton gradeEntryBtn = styledButton("Grade Entry");
        JButton statsBtn = styledButton("Class Statistics");
        JButton downloadCsvBtn = styledButton("Download Grades CSV");

        center.add(assessmentBtn);
        center.add(gradeEntryBtn);
        center.add(statsBtn);
        center.add(downloadCsvBtn);

        add(center, BorderLayout.CENTER);

        // ---- Bottom Bar ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton backBtn = styledButton("Back");
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        // ---- Actions ----
        assessmentBtn.addActionListener(e -> {
            var p = new AssessmentEditorPanel(mainFrame, sectionId);
            mainFrame.loadPanel("assessmentEditor_" + sectionId, p);
            mainFrame.showScreen("assessmentEditor_" + sectionId);
        });

        gradeEntryBtn.addActionListener(e -> {
            GradeEntryPanel p = new GradeEntryPanel(mainFrame, sectionId);
            mainFrame.loadPanel("gradeEntry_" + sectionId, p);
            mainFrame.showScreen("gradeEntry_" + sectionId);
        });

        statsBtn.addActionListener(e -> {
            ClassStatisticsPanel p = new ClassStatisticsPanel(mainFrame, sectionId);
            mainFrame.loadPanel("classStats_" + sectionId, p);
            mainFrame.showScreen("classStats_" + sectionId);
        });

        downloadCsvBtn.addActionListener(e -> handleCSVExport());
        backBtn.addActionListener(e -> mainFrame.showScreen("instructor_sections"));
    }

    // ---- Styled Button ----
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

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

    // ---- CSV Export Wrapper ----
    private void handleCSVExport() {
        try {
            // 1. Determine filename
            String ccode = GradeDAO.getCourseCodeForSection(sectionId);
            if (ccode == null || ccode.isEmpty()) ccode = "section_" + sectionId;

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File(ccode + "_grades.csv"));

            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
                return;

            java.io.File file = chooser.getSelectedFile();

            // 2. Export
            GradeDAO.exportSectionGradesToCSV(sectionId, file.getAbsolutePath());

            JOptionPane.showMessageDialog(this,
                    "CSV exported successfully!\nSaved as: " + file.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to export CSV:\n" + ex.getMessage()
            );
        }
    }
}
