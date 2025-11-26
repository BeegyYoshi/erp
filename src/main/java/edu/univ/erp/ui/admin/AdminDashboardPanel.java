package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {

    private final MainFrame mainFrame;

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton btnUsers = new JButton("Manage Users");
        JButton btnCourses = new JButton("Manage Courses");
        JButton btnSections = new JButton("Manage Sections");
        JButton btnMaintenance = new JButton("Maintenance Mode");
        JButton btnLogout = new JButton("Logout");

        // ===== BUTTON ACTIONS =====

        btnUsers.addActionListener(e -> {
            AdminManageUsersPanel panel = new AdminManageUsersPanel(mainFrame);
            mainFrame.loadPanel("admin_users", panel);
            mainFrame.showScreen("admin_users");
        });

        btnCourses.addActionListener(e -> {
            AdminManageCoursesPanel panel = new AdminManageCoursesPanel(mainFrame);
            mainFrame.loadPanel("admin_courses", panel);
            mainFrame.showScreen("admin_courses");
        });

        btnSections.addActionListener(e -> {
            AdminManageSectionsPanel panel = new AdminManageSectionsPanel(mainFrame);
            mainFrame.loadPanel("admin_sections", panel);
            mainFrame.showScreen("admin_sections");
        });

        btnMaintenance.addActionListener(e -> {
            AdminMaintenancePanel panel = new AdminMaintenancePanel(mainFrame);
            mainFrame.loadPanel("admin_maintenance", panel);
            mainFrame.showScreen("admin_maintenance");
        });

        btnLogout.addActionListener(e -> {
            mainFrame.showScreen("login");
        });

        btnPanel.add(btnUsers);
        btnPanel.add(btnCourses);
        btnPanel.add(btnSections);
        btnPanel.add(btnMaintenance);
        btnPanel.add(btnLogout);

        add(btnPanel, BorderLayout.CENTER);
    }
}
