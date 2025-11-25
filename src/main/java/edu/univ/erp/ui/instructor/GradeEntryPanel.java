package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.InstructorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public GradeEntryPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Grade Entry", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int row, int col) {
                // Only component score cells are editable
                return col >= 1 && col < getColumnCount() - 2;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Grades");
        JButton backBtn = new JButton("Back");

        bottom.add(saveBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> saveGrades());
        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));
    }

    @Override
    public void refresh() {
        model.setRowCount(0);
        model.setColumnCount(0);

        try {
            students = InstructorDAO.getEnrolledStudents(sectionId);
            components = InstructorDAO.getGradeComponents(sectionId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed loading data:\n" + e.getMessage());
            return;
        }

        // Build columns
        model.addColumn("Student");

        for (Map<String,Object> c : components)
            model.addColumn(c.get("component_name"));

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
                row[i+1] = savedScores.getOrDefault(compId, null);
            }

            try {
                if (!savedScores.isEmpty()) {
                    double finalGrade = InstructorDAO.computeFinalGrade(enrollmentId, sectionId);
                    row[row.length-2] = finalGrade;
                    row[row.length-1] = gradeToLetter(finalGrade);
                } else {
                    row[row.length-2] = "";
                    row[row.length-1] = "";
                }
            } catch (SQLException e) {
                row[row.length-2] = "";
                row[row.length-1] = "";
            }

            model.addRow(row);
        }

        revalidate();
        repaint();
    }

    private void saveGrades() {

        for (int r = 0; r < model.getRowCount(); r++) {

            int enrollmentId = (int) students.get(r).get("enrollment_id");

            // Save each component score
            for (int c = 0; c < components.size(); c++) {
                Object val = model.getValueAt(r, c+1);

                if (val == null || val.toString().trim().isEmpty()) {
                    continue; // empty means no update
                }

                try {
                    double score = Double.parseDouble(val.toString().trim());
                    int compId = (int) components.get(c).get("component_id");
                    InstructorDAO.saveOrUpdateScore(enrollmentId, compId, score);
                }
                catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid number for student '"
                                    + model.getValueAt(r,0)
                                    + "' in component '"
                                    + components.get(c).get("component_name") + "'");
                    return;
                }
                catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "DB error saving score:\n" + ex.getMessage());
                    return;
                }
            }

            // Compute final grade
            try {
                double finalGrade = InstructorDAO.computeFinalGrade(enrollmentId, sectionId);
                String letter = gradeToLetter(finalGrade);

                InstructorDAO.saveFinalGrade(enrollmentId, sectionId, finalGrade, letter);

                model.setValueAt(finalGrade, r, model.getColumnCount()-2);
                model.setValueAt(letter, r, model.getColumnCount()-1);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed computing final grade");
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
