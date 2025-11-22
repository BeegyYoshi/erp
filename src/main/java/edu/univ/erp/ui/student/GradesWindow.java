package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class GradesWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("My Grades");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);

        // TODO: Query student's grades

        txt.setText("Loading grades...");

        frame.add(new JScrollPane(txt));
        frame.setVisible(true);
    }
}
