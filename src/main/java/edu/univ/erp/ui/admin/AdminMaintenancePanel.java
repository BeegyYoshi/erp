package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import java.awt.*;

public class AdminMaintenancePanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel statusLabel;

    public AdminMaintenancePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        refreshStatus();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Maintenance Mode", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3,1,10,10));
        center.setBorder(BorderFactory.createEmptyBorder(30,60,30,60));

        statusLabel = new JLabel("Loading...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        center.add(statusLabel);

        JButton toggle = new JButton("Toggle Maintenance Mode");
        center.add(toggle);

        JButton back = new JButton("Back");
        center.add(back);

        toggle.addActionListener(e -> {
            try {
                boolean current = SettingsDAO.isMaintenanceOn();
                SettingsDAO.setMaintenance(!current);
                refreshStatus();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        back.addActionListener(e -> mainFrame.showScreen("admin"));

        add(center, BorderLayout.CENTER);
    }

    private void refreshStatus() {
        try {
            boolean on = SettingsDAO.isMaintenanceOn();
            statusLabel.setText("Maintenance: " + (on ? "ON" : "OFF"));
        } catch (Exception ex) {
            statusLabel.setText("Error");
        }
    }
}
