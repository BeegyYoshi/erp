package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class DropCoursesWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("Drop Courses");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Select a course to drop:");
        frame.add(label, BorderLayout.NORTH);

        // TODO: Load student's enrolled courses

        frame.setVisible(true);
    }
}
