package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SettingsDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class GradeEntryPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int sectionId;

    private JTable table;
    private DefaultTableModel model;

    private List<Map<String,Object>> students;
    private List<Map<String,Object>> components;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public GradeEntryPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Grade Entry", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Table Model ----
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) {
                // Editable only for component scores (between student & final grade)
                return col >= 1 && col < getColumnCount() - 2;
            }
        };

        // ---- Table ----
        table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(PANEL_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));
        table.setGridColor(ACCENT.darker());

        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        // ---- Bottom Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton saveBtn = styledButton("Save Grades");
        JButton backBtn = styledButton("Back");

        bottom.add(saveBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveGrades());
        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));
    }

    // ---- Styled Buttons ----
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

    // ---- Refresh (Load Students + Components) ----
    @Override
    public void refresh() {
        model.setRowCount(0);
        model.setColumnCount(0);

        try {
            students = InstructorDAO.getEnrolledStudents(sectionId);
            components = InstructorDAO.getGradeComponents(sectionId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed loading data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Build columns
        model.addColumn("Student");

        for (Map<String,Object> c : components) {
            model.addColumn(c.get("component_name"));
        }

        model.addColumn("Final Grade");
        model.addColumn("Letter");

        // Populate table
        for (Map<String,Object> st : students) {

            int enrollmentId = (int) st.get("enrollment_id");

            Map<Integer,Double> savedScores;
            try {
                savedScores = InstructorDAO.getScoresForStudent(enrollmentId);
            } catch (SQLException e) {
                savedScores = new HashMap<>();
            }

            Object[] row = new Object[components.size() + 3];
            row[0] = st.get("student");

            for (int i = 0; i < components.size(); i++) {
                int compId = (int) components.get(i).get("component_id");
                row[i + 1] = savedScores.getOrDefault(compId, null);
            }

            try {
                if (!savedScores.isEmpty()) {
                    double finalGrade = InstructorDAO.computeFinalGrade(enrollmentId, sectionId);
                    row[row.length - 2] = finalGrade;
                    row[row.length - 1] = gradeToLetter(finalGrade);
                } else {
                    row[row.length - 2] = "";
                    row[row.length - 1] = "";
                }
            } catch (SQLException e) {
                row[row.length - 2] = "";
                row[row.length - 1] = "";
            }

            model.addRow(row);
        }

        revalidate();
        repaint();
    }

    // ---- Save Grades ----
    private void saveGrades() {

        // Check Maintenance Mode
        try {
            if (SettingsDAO.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Maintenance mode is ON — No grading allowed.",
                        "Access Blocked",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to check maintenance setting:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        for (int r = 0; r < model.getRowCount(); r++) {

            int enrollmentId = (int) students.get(r).get("enrollment_id");

            // Save component scores
            for (int c = 0; c < components.size(); c++) {

                Object val = model.getValueAt(r, c + 1);

                if (val == null || val.toString().trim().isEmpty())
                    continue;

                try {
                    double score = Double.parseDouble(val.toString().trim());
                    int compId = (int) components.get(c).get("component_id");

                    InstructorDAO.saveOrUpdateScore(enrollmentId, compId, score);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid score for student '" + model.getValueAt(r, 0)
                                    + "' in component '" + components.get(c).get("component_name") + "'",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "DB error saving score:\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            }

            // Compute final grade
            try {
                double finalGrade = InstructorDAO.computeFinalGrade(enrollmentId, sectionId);
                String letter = gradeToLetter(finalGrade);

                InstructorDAO.saveFinalGrade(enrollmentId, sectionId, finalGrade, letter);

                model.setValueAt(finalGrade, r, model.getColumnCount() - 2);
                model.setValueAt(letter, r, model.getColumnCount() - 1);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed computing final grade",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        JOptionPane.showMessageDialog(this, "Grades saved!");
    }

    private String gradeToLetter(double g) {
        if (g >= 85) return "A";
        if (g >= 75) return "B";
        if (g >= 65) return "C";
        if (g >= 50) return "D";
        return "F";
    }
}
