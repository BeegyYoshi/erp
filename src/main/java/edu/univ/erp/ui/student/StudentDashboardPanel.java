package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import java.awt.*;

public class StudentDashboardPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int userId;

    public StudentDashboardPanel(MainFrame mainFrame, int userId) {
        this.mainFrame = mainFrame;
        this.userId = userId;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnCatalog = new JButton("View Course Catalog");
        JButton btnRegister = new JButton("Register for Courses");
        JButton btnDrop = new JButton("Drop Courses");
        JButton btnTimetable = new JButton("View Timetable");
        JButton btnGrades = new JButton("View Grades");
        JButton btnLogout = new JButton("Logout");

        // Open Course Catalog
        btnCatalog.addActionListener(e -> {
            CourseCatalogPanel p = new CourseCatalogPanel(mainFrame, userId);
            mainFrame.loadPanel("catalog", p);
            mainFrame.showScreen("catalog");
        });

        // Register Courses
        btnRegister.addActionListener(e -> {
            RegisterCoursesPanel p = new RegisterCoursesPanel(mainFrame, userId);
            mainFrame.loadPanel("register", p);
            mainFrame.showScreen("register");
        });

        // Drop Courses
        btnDrop.addActionListener(e -> {
            DropCoursesPanel p = new DropCoursesPanel(mainFrame, userId);
            mainFrame.loadPanel("dropCourses", p);
            mainFrame.showScreen("dropCourses");
        });

        // Timetable
        btnTimetable.addActionListener(e -> {
            TimetablePanel p = new TimetablePanel(mainFrame, userId);
            mainFrame.loadPanel("timetable", p);
            mainFrame.showScreen("timetable");
        });

        // Grades
        btnGrades.addActionListener(e -> {
            GradesPanel p = new GradesPanel(mainFrame, userId);
            mainFrame.loadPanel("grades", p);
            mainFrame.showScreen("grades");
        });

        // Logout → back to login panel
        btnLogout.addActionListener(e -> {
            // Just show the login screen again
            mainFrame.showScreen("login");
        });

        btnPanel.add(btnCatalog);
        btnPanel.add(btnRegister);
        btnPanel.add(btnDrop);
        btnPanel.add(btnTimetable);
        btnPanel.add(btnGrades);
        btnPanel.add(btnLogout);

        add(btnPanel, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        // Dashboard has no dynamic data yet, but can refresh name/stats later
        revalidate();
        repaint();
    }
}
