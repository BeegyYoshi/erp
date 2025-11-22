package edu.univ.erp.ui.instructor;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard {

    public static void show(int userId) {

        JFrame frame = new JFrame("Instructor Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        JButton btnSections = new JButton("View Assigned Sections");
        JButton btnEnterScores = new JButton("Enter Student Scores");
        JButton btnComputeFinal = new JButton("Compute Final Grades");

        panel.add(btnSections);
        panel.add(btnEnterScores);
        panel.add(btnComputeFinal);

        frame.add(panel);
        frame.setVisible(true);
    }
}
