package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {

    private final MainFrame mainFrame;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ---- Button Panel ----
        JPanel btnPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        btnPanel.setBackground(BG_DARK);

        JButton btnUsers = styledButton("Manage Users");
        JButton btnCourses = styledButton("Manage Courses");
        JButton btnSections = styledButton("Manage Sections");
        JButton btnMaintenance = styledButton("Maintenance Mode");
        JButton btnLogout = styledButton("Logout");

        btnPanel.add(btnUsers);
        btnPanel.add(btnCourses);
        btnPanel.add(btnSections);
        btnPanel.add(btnMaintenance);
        btnPanel.add(btnLogout);

        add(btnPanel, BorderLayout.CENTER);

        // ---- Actions ----
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

        btnLogout.addActionListener(e -> mainFrame.showScreen("login"));
    }

    // ---- Styled Button ----
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Hover effect
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
