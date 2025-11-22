package edu.univ.erp.ui;

import edu.univ.erp.auth.LoginService;
import edu.univ.erp.auth.LoginResult;

import javax.swing.*;
import java.awt.*;

public class LoginWindow {

    public static void show() {
        SwingUtilities.invokeLater(() -> new LoginWindow().create());
    }

    private void create() {
        JFrame frame = new JFrame("University ERP - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with padding
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Username label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        // Username field
        JTextField userField = new JTextField(18);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        // Password label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        // Password field
        JPasswordField passField = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 32));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginBtn, gbc);

        // Button action
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            LoginResult result = LoginService.login(username, password);
            if (!result.ok) {
                JOptionPane.showMessageDialog(frame, result.error, "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            frame.dispose();
            switch (result.role) {
                case "admin" -> edu.univ.erp.ui.admin.AdminDashboard.show(result.userId);
                case "student" -> edu.univ.erp.ui.student.StudentDashboard.show(result.userId);
                case "instructor" -> edu.univ.erp.ui.instructor.InstructorDashboard.show(result.userId);
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // center screen
        frame.setVisible(true);
    }
}
