package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.auth.PasswordHasher;

import javax.swing.*;
import java.awt.*;

public class AdminManageUsersPanel extends JPanel {

    private final MainFrame mainFrame;

    // ---- Theme colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminManageUsersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---------- TITLE ----------
        JLabel title = new JLabel("Manage Users", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        add(title, BorderLayout.NORTH);

        // ---------- FORM ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Username Label + Field
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(styledLabel("Username:"), gbc);

        JTextField txtUser = styledField();
        gbc.gridx = 1;
        form.add(txtUser, gbc);

        // Role Label + ComboBox
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(styledLabel("Role:"), gbc);

        JComboBox<String> roleBox = styledComboBox(new String[]{"student", "instructor", "admin"});
        gbc.gridx = 1;
        form.add(roleBox, gbc);

        // Password Label + Field
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(styledLabel("Password:"), gbc);

        JPasswordField txtPass = styledPasswordField();
        gbc.gridx = 1;
        form.add(txtPass, gbc);

        // Create User Button
        JButton btnCreate = styledButton("Create User");
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(btnCreate, gbc);

        // Back Button
        JButton btnBack = styledButton("Back");
        gbc.gridy = 4;
        form.add(btnBack, gbc);

        add(form, BorderLayout.CENTER);

        // ---------- ACTIONS ----------
        btnCreate.addActionListener(e -> {
            try {
                String user = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword()).trim();
                String role = roleBox.getSelectedItem().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and password required.");
                    return;
                }

                String hash = PasswordHasher.hash(pass);

                var conn = edu.univ.erp.data.AuthDB.getConnection();
                var ps = conn.prepareStatement(
                        "INSERT INTO users_auth(username, role, password_hash) VALUES (?,?,?)"
                );

                ps.setString(1, user);
                ps.setString(2, role);
                ps.setString(3, hash);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "User created!");

                txtUser.setText("");
                txtPass.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error creating user:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnBack.addActionListener(e -> mainFrame.showScreen("admin"));
    }

    // -------------------- COMPONENT STYLING --------------------

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        return lbl;
    }

    private JTextField styledField() {
        JTextField tf = new JTextField(20);
        tf.setBackground(PANEL_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        return tf;
    }

    private JPasswordField styledPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setBackground(PANEL_DARK);
        pf.setForeground(TEXT_LIGHT);
        pf.setCaretColor(TEXT_LIGHT);
        pf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        return pf;
    }

    private JComboBox<String> styledComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("SansSerif", Font.PLAIN, 15));
        box.setBackground(PANEL_DARK);
        box.setForeground(TEXT_LIGHT);
        box.setBorder(BorderFactory.createLineBorder(ACCENT));
        return box;
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }
}
