package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboardPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int instructorId;

    public InstructorDashboardPanel(MainFrame mainFrame, int instructorId) {
        this.mainFrame = mainFrame;
        this.instructorId = instructorId;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // Menu Buttons
        JPanel menu = new JPanel(new GridLayout(4, 1, 10, 10));
        menu.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JButton btnMySections = new JButton("My Sections");
        JButton btnLogout = new JButton("Logout");
        btnMySections.addActionListener(e -> {
            MySectionsPanel p = new MySectionsPanel(mainFrame, instructorId);
            mainFrame.loadPanel("instructor_sections", p);
            mainFrame.showScreen("instructor_sections");
        });

        btnLogout.addActionListener(e -> mainFrame.showScreen("login"));

        menu.add(btnMySections);
        menu.add(btnLogout);

        add(menu, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        revalidate();
        repaint();
    }
}
