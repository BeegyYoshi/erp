package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class RegisterCoursesWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("Register for Courses");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Select a course to register:");
        frame.add(label, BorderLayout.NORTH);

        // TODO: Load all available course sections for registration

        frame.setVisible(true);
    }
}
