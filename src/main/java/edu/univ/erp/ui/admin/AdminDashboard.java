package edu.univ.erp.ui.admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard {

    public static void show(int userId) {

        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        JButton btnAddUser = new JButton("Create User (Student/Instructor/Admin)");
        JButton btnCreateCourse = new JButton("Create Courses");
        JButton btnCreateSection = new JButton("Create Sections");
        JButton btnAssignInstructor = new JButton("Assign Instructor to Section");
        JButton btnMaintenance = new JButton("Toggle Maintenance Mode");

        panel.add(btnAddUser);
        panel.add(btnCreateCourse);
        panel.add(btnCreateSection);
        panel.add(btnAssignInstructor);
        panel.add(btnMaintenance);

        frame.add(panel);
        frame.setVisible(true);
    }
}
