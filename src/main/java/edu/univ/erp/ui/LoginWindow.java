package edu.univ.erp.ui;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

import edu.univ.erp.auth.LoginService;
import edu.univ.erp.ui.student.StudentDashboard;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.admin.AdminDashboard;

public class LoginWindow {

    public static void show() {
        FlatLightLaf.setup();

        JFrame frame = new JFrame("University ERP - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(150, 50, 180, 30);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 100, 180, 30);
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 150, 100, 30);
        panel.add(loginBtn);
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            var result = LoginService.login(user, pass);

            if (!result.ok) {
                JOptionPane.showMessageDialog(frame, result.error);
                return;
            }

            frame.dispose();

            switch (result.role) {
                case "student" -> StudentDashboard.show(result.userId);
                case "instructor" -> InstructorDashboard.show(result.userId);
                case "admin" -> AdminDashboard.show(result.userId);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}

