package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.EnrollmentDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class RegisterCoursesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JPanel listPanel;
    private List<JCheckBox> checkboxes = new ArrayList<>();
    private List<Map<String, Object>> sections = new ArrayList<>();

    public RegisterCoursesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Select the sections you want to register:");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton registerBtn = new JButton("Register Selected");
        JButton backBtn = new JButton("Back");

        bottom.add(registerBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> registerSelected());
        backBtn.addActionListener(e -> mainFrame.showScreen("student"));
    }

    @Override
    public void refresh() {
        listPanel.removeAll();
        checkboxes.clear();
        sections.clear();

        try {
            sections = SectionDAO.fetchAvailableSections(studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sections.\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Map<String, Object> s : sections) {
            int sectionId = (int) s.get("section_id");
            String label =
                    s.get("code") + " - " + s.get("title")
                            + " | Section: " + sectionId
                            + " | " + s.get("day_time")
                            + " | Room: " + s.get("room");

            JCheckBox box = new JCheckBox(label);
            checkboxes.add(box);
            listPanel.add(box);
            listPanel.add(Box.createVerticalStrut(5));
        }

        revalidate();
        repaint();
    }

    private void registerSelected() {
        boolean anySelected = false;

        for (int i = 0; i < checkboxes.size(); i++) {
            if (checkboxes.get(i).isSelected()) {
                anySelected = true;
                int sectionId = (int) sections.get(i).get("section_id");

                try {
                    boolean ok = EnrollmentDAO.enrollStudent(studentId, sectionId);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this,
                                "Already enrolled in section " + sectionId);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error enrolling in section " + sectionId + ":\n" + ex.getMessage());
                }
            }
        }

        if (!anySelected) {
            JOptionPane.showMessageDialog(this, "Please select at least one section.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Registration complete!");

        refresh(); // reload course list after registering
    }
}
