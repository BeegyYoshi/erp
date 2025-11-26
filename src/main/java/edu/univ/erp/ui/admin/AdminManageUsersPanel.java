package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.AuthDAO;

import javax.swing.*;
import java.awt.*;

public class AdminManageUsersPanel extends JPanel {

    private final MainFrame mainFrame;

    public AdminManageUsersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Manage Users", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Username:"), gbc);

        JTextField txtUser = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtUser, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Role:"), gbc);

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"student", "instructor", "admin"});
        gbc.gridx = 1;
        form.add(roleBox, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Password:"), gbc);

        JPasswordField txtPass = new JPasswordField(20);
        gbc.gridx = 1;
        form.add(txtPass, gbc);

        JButton btnCreate = new JButton("Create User");
        gbc.gridx = 1; gbc.gridy = 3;
        form.add(btnCreate, gbc);

        btnCreate.addActionListener(e -> {
            try {
                String user = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword());
                String role = roleBox.getSelectedItem().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and password required");
                    return;
                }

                String hash = PasswordHasher.hash(pass);

                // insert directly using SQL
                var conn = edu.univ.erp.data.AuthDB.getConnection();
                var ps = conn.prepareStatement(
                    "INSERT INTO users_auth(username, role, password_hash) VALUES(?,?,?)"
                );

                ps.setString(1, user);
                ps.setString(2, role);
                ps.setString(3, hash);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "User created successfully!");

                txtUser.setText("");
                txtPass.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage());
            }
        });

        // back button
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> mainFrame.showScreen("admin"));
        gbc.gridy = 4;
        form.add(btnBack, gbc);

        add(form, BorderLayout.CENTER);
    }
}
