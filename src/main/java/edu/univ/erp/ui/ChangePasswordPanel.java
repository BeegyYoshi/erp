package edu.univ.erp.ui;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AuthDAO;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {

    private final MainFrame mainFrame;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color ACCENT = Color.decode("#39AEA8");
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);

    public ChangePasswordPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {

        setLayout(new GridBagLayout());
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER;

        // ---- Main Box ----
        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(PANEL_DARK);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.WEST;

        // ---- Title ----
        JLabel title = new JLabel("CHANGE PASSWORD");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(ACCENT);

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        box.add(title, c);

        // ---- Labels & Fields ----
        JLabel userLabel = styledLabel("Username:");
        JTextField userField = styledTextField();

        JLabel oldLabel = styledLabel("Old Password:");
        JPasswordField oldField = styledPasswordField();

        JLabel newLabel = styledLabel("New Password:");
        JPasswordField newField = styledPasswordField();

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 1; c.gridx = 0; box.add(userLabel, c);
        c.gridx = 1; box.add(userField, c);

        c.gridy = 2; c.gridx = 0; box.add(oldLabel, c);
        c.gridx = 1; box.add(oldField, c);

        c.gridy = 3; c.gridx = 0; box.add(newLabel, c);
        c.gridx = 1; box.add(newField, c);

        // ---- Buttons ----
        JButton updateBtn = styledButton("Update Password");
        JButton backBtn = styledButton("Back");

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(PANEL_DARK);
        btnPanel.add(updateBtn);
        btnPanel.add(backBtn);

        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        box.add(btnPanel, c);

        // Add box to panel
        add(box, gbc);

        // ---- Button Actions ----
        updateBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String oldPass = new String(oldField.getPassword()).trim();
            String newPass = new String(newField.getPassword()).trim();

            if (username.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            try {
                boolean ok = AuthDAO.changePassword(username, oldPass, newPass);
                if (!ok) {
                    JOptionPane.showMessageDialog(this,
                            "Incorrect username or old password.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                mainFrame.showScreen("login");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> mainFrame.showScreen("login"));
    }

    // -------- Styled Components --------

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
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
