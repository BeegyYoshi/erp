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

    public ClassStatisticsPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Class Statistics", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(6, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        avgLabel = new JLabel();
        highestLabel = new JLabel();
        lowestLabel = new JLabel();
        passLabel = new JLabel();
        totalLabel = new JLabel();

        statsPanel.add(avgLabel);
        statsPanel.add(highestLabel);
        statsPanel.add(lowestLabel);
        statsPanel.add(passLabel);
        statsPanel.add(totalLabel);

        add(statsPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back");
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));
    }

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
            JOptionPane.showMessageDialog(this,
                    "Failed to load statistics:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
