package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboardPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int instructorId;

    private JPanel maintenanceBanner;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public InstructorDashboardPanel(MainFrame mainFrame, int instructorId) {
        this.mainFrame = mainFrame;
        this.instructorId = instructorId;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---------- Maintenance Banner ----------
        maintenanceBanner = new JPanel();
        maintenanceBanner.setBackground(new Color(180, 60, 60));

        JLabel warn = new JLabel(
                "Maintenance mode is ON — You can only VIEW and cannot make changes.",
                SwingConstants.CENTER
        );
        warn.setFont(new Font("SansSerif", Font.BOLD, 14));
        warn.setForeground(Color.WHITE);

        maintenanceBanner.add(warn);
        maintenanceBanner.setVisible(false);

        // ---------- Title ----------
        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // ---------- Top Container ----------
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(BG_DARK);

        top.add(maintenanceBanner);
        top.add(title);

        add(top, BorderLayout.NORTH);

        // ---------- Buttons ----------
        JPanel menu = new JPanel(new GridLayout(2, 1, 15, 15));
        menu.setBorder(BorderFactory.createEmptyBorder(25, 80, 25, 80));
        menu.setBackground(BG_DARK);

        JButton btnMySections = styledButton("My Sections");
        JButton btnLogout = styledButton("Logout");

        menu.add(btnMySections);
        menu.add(btnLogout);

        add(menu, BorderLayout.CENTER);

        // ---------- Actions ----------
        btnMySections.addActionListener(e -> {
            MySectionsPanel p = new MySectionsPanel(mainFrame, instructorId);
            mainFrame.loadPanel("instructor_sections", p);
            mainFrame.showScreen("instructor_sections");
        });

        btnLogout.addActionListener(e -> mainFrame.showScreen("login"));
    }

    // ---------- Styled Button ----------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

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

    @Override
    public void refresh() {
        try {
            boolean maintenance = SettingsDAO.isMaintenanceOn();
            maintenanceBanner.setVisible(maintenance);
        } catch (Exception e) {
            maintenanceBanner.setVisible(false);
        }

        revalidate();
        repaint();
    }
}
