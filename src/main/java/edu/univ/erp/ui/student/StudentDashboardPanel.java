package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import java.awt.*;

public class StudentDashboardPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int userId;
    private JPanel maintenanceBanner;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color ACCENT = Color.decode("#39AEA8");
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color PANEL_DARK = new Color(45, 45, 45);

    public StudentDashboardPanel(MainFrame mainFrame, int userId) {
        this.mainFrame = mainFrame;
        this.userId = userId;
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
        warn.setForeground(Color.WHITE);
        warn.setFont(new Font("SansSerif", Font.BOLD, 14));

        maintenanceBanner.add(warn);
        maintenanceBanner.setVisible(false);

        // ---------- Title ----------
        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // ---------- Top Layout ----------
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_DARK);

        topPanel.add(maintenanceBanner);
        topPanel.add(title);

        add(topPanel, BorderLayout.NORTH);

        // ---------- Button Panel ----------
        JPanel btnPanel = new JPanel(new GridLayout(6, 1, 12, 12));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        btnPanel.setBackground(BG_DARK);

        JButton btnCatalog = styledButton("View Course Catalog");
        JButton btnRegister = styledButton("Register for Courses");
        JButton btnDrop = styledButton("Drop Courses");
        JButton btnTimetable = styledButton("View Timetable");
        JButton btnGrades = styledButton("View Grades");
        JButton btnLogout = styledButton("Logout");

        // ---------- Button Actions ----------
        btnCatalog.addActionListener(e -> {
            CourseCatalogPanel p = new CourseCatalogPanel(mainFrame, userId);
            mainFrame.loadPanel("catalog", p);
            mainFrame.showScreen("catalog");
        });

        btnRegister.addActionListener(e -> {
            RegisterCoursesPanel p = new RegisterCoursesPanel(mainFrame, userId);
            mainFrame.loadPanel("register", p);
            mainFrame.showScreen("register");
        });

        btnDrop.addActionListener(e -> {
            DropCoursesPanel p = new DropCoursesPanel(mainFrame, userId);
            mainFrame.loadPanel("dropCourses", p);
            mainFrame.showScreen("dropCourses");
        });

        btnTimetable.addActionListener(e -> {
            TimetablePanel p = new TimetablePanel(mainFrame, userId);
            mainFrame.loadPanel("timetable", p);
            mainFrame.showScreen("timetable");
        });

        btnGrades.addActionListener(e -> {
            GradesPanel p = new GradesPanel(mainFrame, userId);
            mainFrame.loadPanel("grades", p);
            mainFrame.showScreen("grades");
        });

        btnLogout.addActionListener(e -> {
            mainFrame.showScreen("login");
        });

        // Add to panel
        btnPanel.add(btnCatalog);
        btnPanel.add(btnRegister);
        btnPanel.add(btnDrop);
        btnPanel.add(btnTimetable);
        btnPanel.add(btnGrades);
        btnPanel.add(btnLogout);

        add(btnPanel, BorderLayout.CENTER);
    }

    // ---------- Styled Components ----------

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
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

    @Override
    public void refresh() {
        try {
            boolean maintenance = SettingsDAO.isMaintenanceOn();
            maintenanceBanner.setVisible(maintenance);

            maintenanceBanner.revalidate();
            maintenanceBanner.repaint();

            JPanel parent = (JPanel) maintenanceBanner.getParent();
            parent.revalidate();
            parent.repaint();

        } catch (Exception e) {
            maintenanceBanner.setVisible(false);
        }
    }
}
