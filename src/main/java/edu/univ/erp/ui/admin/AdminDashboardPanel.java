package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.SettingsDAO;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;

    // ---- Theme Colors ----
    private static final Color BG_DARK   = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT     = Color.decode("#39AEA8");

    private JPanel maintenanceBanner;

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---------- Maintenance Banner ----------
        maintenanceBanner = new JPanel();
        maintenanceBanner.setBackground(new Color(200, 70, 70));

        JLabel warn = new JLabel(
                "Maintenance mode is ON — You can only VIEW and cannot make changes.",
                SwingConstants.CENTER
        );
        warn.setForeground(Color.WHITE);
        warn.setFont(new Font("SansSerif", Font.BOLD, 14));

        maintenanceBanner.add(warn);
        maintenanceBanner.setVisible(false);  // initially hidden

        // ---------- Title ----------
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Put banner + title together
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_DARK);
        topPanel.add(maintenanceBanner);
        topPanel.add(title);

        add(topPanel, BorderLayout.NORTH);

        // ---------- Button Panel ----------
        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 15, 15));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        btnPanel.setBackground(BG_DARK);

        JButton btnUsers        = styledButton("Manage Users");
        JButton btnCourses      = styledButton("Manage Courses");
        JButton btnSections     = styledButton("Manage Sections");
        JButton btnMaintenance  = styledButton("Maintenance Mode");
        JButton btnBackup       = styledButton("Backup / Restore Database");
        JButton btnLogout       = styledButton("Logout");

        btnPanel.add(btnUsers);
        btnPanel.add(btnCourses);
        btnPanel.add(btnSections);
        btnPanel.add(btnMaintenance);
        btnPanel.add(btnBackup);
        btnPanel.add(btnLogout);

        add(btnPanel, BorderLayout.CENTER);

        // ---------- Actions ----------
        btnUsers.addActionListener(e -> {
            var p = new AdminManageUsersPanel(mainFrame);
            mainFrame.loadPanel("admin_users", p);
            mainFrame.showScreen("admin_users");
        });

        btnCourses.addActionListener(e -> {
            var p = new AdminManageCoursesPanel(mainFrame);
            mainFrame.loadPanel("admin_courses", p);
            mainFrame.showScreen("admin_courses");
        });

        btnSections.addActionListener(e -> {
            var p = new AdminManageSectionsPanel(mainFrame);
            mainFrame.loadPanel("admin_sections", p);
            mainFrame.showScreen("admin_sections");
        });

        btnMaintenance.addActionListener(e -> {
            var p = new AdminMaintenancePanel(mainFrame);
            mainFrame.loadPanel("admin_maintenance", p);
            mainFrame.showScreen("admin_maintenance");
        });

        btnBackup.addActionListener(e -> {
            var p = new AdminBackupPanel(mainFrame);
            mainFrame.loadPanel("admin_backup", p);
            mainFrame.showScreen("admin_backup");
        });

        btnLogout.addActionListener(e -> mainFrame.showScreen("login"));
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

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

    // -------- Refresh (called automatically by MainFrame if implemented correctly) --------
    @Override
    public void refresh() {
        try {
            boolean m = SettingsDAO.isMaintenanceOn();
            maintenanceBanner.setVisible(m);
        } catch (Exception e) {
            maintenanceBanner.setVisible(false);
        }

        revalidate();
        repaint();
    }
}
