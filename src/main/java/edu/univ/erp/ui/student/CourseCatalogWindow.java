package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class CourseCatalogWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("Course Catalog");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);

        // TODO: Call StudentService.getAllCourses()
        txt.setText("Loading courses...");

        frame.add(new JScrollPane(txt));
        frame.setVisible(true);
    }
}
