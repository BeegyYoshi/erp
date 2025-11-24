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
                // Student name + final grade + letter grade NOT editable
                return col >= 1 && col < getColumnCount()-2;
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

        // Build column headers
        model.addColumn("Student");
        for (Map<String,Object> c : components) {
            model.addColumn(c.get("component_name"));
        }
        model.addColumn("Final Grade");
        model.addColumn("Letter");

        // Add rows
        for (Map<String,Object> st : students) {
            Object[] row = new Object[components.size() + 3];
            row[0] = st.get("student");

            for (int i = 0; i < components.size(); i++)
                row[i+1] = "";  // blank score entry

            row[row.length-2] = ""; // final
            row[row.length-1] = ""; // letter

            model.addRow(row);
        }

        revalidate();
        repaint();
    }

    private void saveGrades() {
        for (int r = 0; r < model.getRowCount(); r++) {

            int enrollmentId = (int) students.get(r).get("enrollment_id");

            double finalGrade = computeFinalGrade(r);
            String letter = gradeToLetter(finalGrade);

            try {
                InstructorDAO.saveFinalGrade(enrollmentId, finalGrade, letter);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving grade for row " + r);
            }

            model.setValueAt(finalGrade, r, model.getColumnCount()-2);
            model.setValueAt(letter, r, model.getColumnCount()-1);
        }

        JOptionPane.showMessageDialog(this, "Grades saved!");
    }

    private double computeFinalGrade(int row) {
        double total = 0;

        for (int i = 0; i < components.size(); i++) {
            String value = model.getValueAt(row, i+1).toString().trim();
            if (!value.isEmpty()) {
                try {
                    double score = Double.parseDouble(value);
                    double weight = ((Number) components.get(i).get("weight")).doubleValue();
                    total += score * (weight / 100.0);
                } catch (Exception ignore) {}
            }
        }

        return total;
    }

    private String gradeToLetter(double g) {
        if (g >= 85) return "A";
        if (g >= 75) return "B";
        if (g >= 65) return "C";
        if (g >= 50) return "D";
        return "F";
    }
}
