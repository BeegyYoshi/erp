package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import java.awt.*;

public class AdminMaintenancePanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel statusLabel;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminMaintenancePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        refreshStatus();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ---- Center Area ----
        JPanel center = new JPanel(new GridLayout(3, 1, 12, 12));
        center.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        center.setBackground(BG_DARK);

        statusLabel = new JLabel("Loading...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        statusLabel.setForeground(TEXT_LIGHT);

        JButton toggle = styledButton("Toggle Maintenance Mode");
        JButton back = styledButton("Back");

        center.add(statusLabel);
        center.add(toggle);
        center.add(back);

        add(center, BorderLayout.CENTER);

        // ---- Actions ----
        toggle.addActionListener(e -> {
            try {
                boolean current = SettingsDAO.isMaintenanceOn();
                SettingsDAO.setMaintenance(!current);
                refreshStatus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to update maintenance mode:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        back.addActionListener(e -> mainFrame.showScreen("admin"));
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    private void refreshStatus() {
        try {
            boolean on = SettingsDAO.isMaintenanceOn();
            statusLabel.setText("Maintenance: " + (on ? "ON" : "OFF"));
            statusLabel.setForeground(on ? Color.RED : ACCENT);
        } catch (Exception ex) {
            statusLabel.setText("Error");
            statusLabel.setForeground(Color.RED);
        }
    }
}
