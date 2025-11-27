package edu.univ.erp.ui;

import edu.univ.erp.MainFrame;
import edu.univ.erp.auth.LoginResult;
import edu.univ.erp.auth.LoginService;
import edu.univ.erp.ui.instructor.InstructorDashboardPanel;
import edu.univ.erp.ui.student.StudentDashboardPanel;
import edu.univ.erp.ui.admin.AdminDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;

    private int failedAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color ACCENT = Color.decode("#39AEA8");
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {

        setLayout(new GridBagLayout());
        setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER;

        // ---------- Center Panel ----------
        JPanel loginBox = new JPanel();
        loginBox.setLayout(new GridBagLayout());
        loginBox.setBackground(PANEL_DARK);
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        // -------- Title --------
        JLabel title = new JLabel("UNIVERSITY ERP");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ACCENT);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        loginBox.add(title, c);

        // -------- Username --------
        JLabel userLabel = styledLabel("Username:");
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
        loginBox.add(userLabel, c);

        JTextField userField = styledTextField();
        c.gridx = 1;
        loginBox.add(userField, c);

        // -------- Password --------
        JLabel passLabel = styledLabel("Password:");
        c.gridx = 0; c.gridy = 2;
        loginBox.add(passLabel, c);

        JPasswordField passField = styledPasswordField();
        c.gridx = 1;
        loginBox.add(passField, c);

        // -------- Buttons --------
        JButton loginBtn = styledButton("Login");
        JButton changePassBtn = styledButton("Change Password");

        // Login
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        loginBox.add(loginBtn, c);

        // Change Password
        c.gridy = 4;
        loginBox.add(changePassBtn, c);

        // Add main container
        add(loginBox, gbc);

        // ---------- Change Password ----------
        changePassBtn.addActionListener(e -> {
            ChangePasswordPanel cp = new ChangePasswordPanel(mainFrame);
            mainFrame.loadPanel("changePassword", cp);
            mainFrame.showScreen("changePassword");
        });

        // ---------- LOGIN ACTION ----------
        loginBtn.addActionListener(e -> {

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
                    loginBtn.setEnabled(false);
                    loginBtn.setBackground(new Color(120, 40, 40));
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

            failedAttempts = 0; // reset

            JPanel dashboardPanel = switch (result.role) {
                case "student" -> new StudentDashboardPanel(mainFrame, result.userId);
                case "instructor" -> new InstructorDashboardPanel(mainFrame, result.userId);
                case "admin" -> new AdminDashboardPanel(mainFrame);
                default -> null;
            };

            if (dashboardPanel == null) {
                JOptionPane.showMessageDialog(this, "Unknown role!");
                return;
            }

            mainFrame.loadPanel(result.role, dashboardPanel);
            mainFrame.showScreen(result.role);
        });
    }

    // ------------ Styled Components ------------

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField(16);
        tf.setBackground(new Color(60, 60, 60));
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        return tf;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField(16);
        pf.setBackground(new Color(60, 60, 60));
        pf.setForeground(TEXT_LIGHT);
        pf.setCaretColor(TEXT_LIGHT);
        pf.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        return pf;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }
}
