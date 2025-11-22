package edu.univ.erp.ui.student;

import edu.univ.erp.ui.student.CourseCatalogWindow;
import javax.swing.*;
import java.awt.*;

public class StudentDashboard {

    public static void show(int userId) {

        JFrame frame = new JFrame("Student Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        JButton btnCatalog = new JButton("View Course Catalog");
        JButton btnRegister = new JButton("Register for Courses");
        JButton btnDrop = new JButton("Drop Courses");
        JButton btnTimetable = new JButton("View Timetable");
        JButton btnGrades = new JButton("View Grades");

        btnCatalog.addActionListener(e -> CourseCatalogWindow.show(userId));
        btnRegister.addActionListener(e -> RegisterCoursesWindow.show(userId));
        btnDrop.addActionListener(e -> DropCoursesWindow.show(userId));
        btnTimetable.addActionListener(e -> TimetableWindow.show(userId));
        btnGrades.addActionListener(e -> GradesWindow.show(userId));


        panel.add(btnCatalog);
        panel.add(btnRegister);
        panel.add(btnDrop);
        panel.add(btnTimetable);
        panel.add(btnGrades);

        frame.add(panel);
        frame.setVisible(true);
    }
}
