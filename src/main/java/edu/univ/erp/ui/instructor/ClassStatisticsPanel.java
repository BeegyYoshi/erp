package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.GradeDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class ClassStatisticsPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int sectionId;

    private JLabel avgLabel;
    private JLabel highestLabel;
    private JLabel lowestLabel;
    private JLabel passLabel;
    private JLabel totalLabel;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public ClassStatisticsPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Class Statistics", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Stats Panel ----
        JPanel statsPanel = new JPanel(new GridLayout(6, 1, 12, 12));
        statsPanel.setBackground(BG_DARK);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        avgLabel = styledStatLabel();
        highestLabel = styledStatLabel();
        lowestLabel = styledStatLabel();
        passLabel = styledStatLabel();
        totalLabel = styledStatLabel();

        statsPanel.add(avgLabel);
        statsPanel.add(highestLabel);
        statsPanel.add(lowestLabel);
        statsPanel.add(passLabel);
        statsPanel.add(totalLabel);

        add(statsPanel, BorderLayout.CENTER);

        // ---- Bottom Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton backBtn = styledButton("Back");
        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));

        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // ---- Styled Statistic Label ----
    private JLabel styledStatLabel() {
        JLabel lbl = new JLabel();
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
        return lbl;
    }

    // ---- Styled Button ----
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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

    // ---- Refresh Stats ----
    @Override
    public void refresh() {
        try {
            Map<String, Object> summary = GradeDAO.getClassStatistics(sectionId);

            avgLabel.setText("Average Grade: " + summary.get("average"));
            highestLabel.setText("Highest Grade: " + summary.get("highest"));
            lowestLabel.setText("Lowest Grade: " + summary.get("lowest"));
            passLabel.setText("Pass Percentage: " + summary.get("pass_percentage") + "%");
            totalLabel.setText("Total Students: " + summary.get("total"));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load statistics:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
