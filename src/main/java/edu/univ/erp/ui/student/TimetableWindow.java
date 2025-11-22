package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class TimetableWindow {

    public static void show(int studentId) {
        JFrame frame = new JFrame("Timetable");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);

        // TODO: Query student's sections with day, time, room

        txt.setText("Loading timetable...");

        frame.add(new JScrollPane(txt));
        frame.setVisible(true);
    }
}
