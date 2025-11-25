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

    private int failedAttempts = 0;      // 🔥 Track failed attempts
    private static final int MAX_ATTEMPTS = 3;

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

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);

        JTextField userField = new JTextField(18);
        gbc.gridx = 1;
        add(userField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);

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
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setPreferredSize(new Dimension(150, 28));

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(changePassBtn, gbc);

        changePassBtn.addActionListener(e -> {
            ChangePasswordPanel cp = new ChangePasswordPanel(mainFrame);
            mainFrame.loadPanel("changePassword", cp);
            mainFrame.showScreen("changePassword");
        });

        // Action
        loginBtn.addActionListener(e -> {

            // 🔥 Stop login if locked out
            if (failedAttempts >= MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(this,
                        "Too many failed attempts.\nLogin is locked.",
                        "Locked Out",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            LoginResult result = LoginService.login(username, password);

            if (!result.ok) {
                failedAttempts++;

                if (failedAttempts >= MAX_ATTEMPTS) {
                    loginBtn.setEnabled(false);   // disable button
                    JOptionPane.showMessageDialog(this,
                            "Too many failed attempts.\nLogin disabled.",
                            "Locked",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid username or password.\nAttempts left: " +
                                    (MAX_ATTEMPTS - failedAttempts),
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                return;
            }

            // Successful login resets attempt count
            failedAttempts = 0;

            // Load dashboard panel
            JPanel dashboardPanel = switch (result.role) {
                case "student" ->
                        new StudentDashboardPanel(mainFrame, result.userId);
                case "instructor" ->
                        new InstructorDashboardPanel(mainFrame, result.userId);
                default -> {
                    JOptionPane.showMessageDialog(this, "Unknown role!");
                    yield null;
                }
            };

            if (dashboardPanel == null) return;

            mainFrame.loadPanel(result.role, dashboardPanel);
            mainFrame.showScreen(result.role);
        });
    }
}
