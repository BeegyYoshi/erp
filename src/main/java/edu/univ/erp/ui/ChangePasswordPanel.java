package edu.univ.erp.ui;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.AuthDAO;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {

    private final MainFrame mainFrame;

    public ChangePasswordPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(18);

        JLabel oldLabel = new JLabel("Old Password:");
        JPasswordField oldField = new JPasswordField(18);

        JLabel newLabel = new JLabel("New Password:");
        JPasswordField newField = new JPasswordField(18);

        gbc.gridy = 1; gbc.gridx = 0; add(userLabel, gbc);
        gbc.gridx = 1; add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0; add(oldLabel, gbc);
        gbc.gridx = 1; add(oldField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; add(newLabel, gbc);
        gbc.gridx = 1; add(newField, gbc);

        JButton updateBtn = new JButton("Update Password");
        JButton backBtn = new JButton("Back");

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel btnPanel = new JPanel();
        btnPanel.add(updateBtn);
        btnPanel.add(backBtn);

        add(btnPanel, gbc);

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
}
