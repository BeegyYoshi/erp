package edu.univ.erp.ui;

import edu.univ.erp.MainFrame;
import edu.univ.erp.auth.LoginResult;
import edu.univ.erp.auth.LoginService;
import edu.univ.erp.ui.instructor.InstructorDashboardPanel;
import edu.univ.erp.ui.student.StudentDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Username label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);

        // Username field
        JTextField userField = new JTextField(18);
        gbc.gridx = 1;
        add(userField, gbc);

        // Password label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);

        // Password field
        JPasswordField passField = new JPasswordField(18);
        gbc.gridx = 1;
        add(passField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 32));

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginBtn, gbc);

        // Login Action
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            LoginResult result = LoginService.login(username, password);
            if (!result.ok) {
                JOptionPane.showMessageDialog(
                        this, result.error, "Login Error", JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Create the dashboard panel based on role
            JPanel dashboardPanel = switch (result.role) {
                // case "admin"      -> new edu.univ.erp.ui.admin.AdminDashboard(mainFrame, result.userId);
                case "student"    -> new StudentDashboardPanel(mainFrame, result.userId);
                case "instructor" -> new InstructorDashboardPanel(mainFrame, result.userId);
                default -> {
                    JOptionPane.showMessageDialog(this, "Unknown role!");
                    yield null;
                }
            };

            if (dashboardPanel == null) return;

            // Load and switch
            mainFrame.loadPanel(result.role, dashboardPanel);
            mainFrame.showScreen(result.role);
        });
    }
}
