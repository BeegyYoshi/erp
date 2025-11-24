package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.InstructorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AssessmentEditorPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int sectionId;

    private JTable table;
    private DefaultTableModel model;

    private JTextField nameField;
    private JTextField weightField;

    public AssessmentEditorPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Assessment Components", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Only 2 columns now: Component + Weight
        String[] cols = {"Component", "Weight (%)"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // bottom panel (add + delete)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        nameField = new JTextField(12);
        weightField = new JTextField(5);

        JButton addBtn = new JButton("Add");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton backBtn = new JButton("Back");

        bottom.add(new JLabel("Name:"));
        bottom.add(nameField);
        bottom.add(new JLabel("Weight:"));
        bottom.add(weightField);
        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addComponent());
        deleteBtn.addActionListener(e -> deleteSelectedComponent());
        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));
    }

    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            List<Map<String, Object>> rows = InstructorDAO.getGradeComponents(sectionId);

            for (Map<String, Object> r : rows) {
                model.addRow(new Object[]{
                        r.get("component_name"),
                        r.get("weight")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load components\n" + e.getMessage());
        }
    }

    private void addComponent() {
        String name = nameField.getText().trim();
        double weight;

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a component name");
            return;
        }

        try {
            weight = Double.parseDouble(weightField.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric weight");
            return;
        }

        try {
            boolean ok = InstructorDAO.addGradeComponent(sectionId, name, weight);

            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Cannot add: Total weight exceeds 100%");
                return;
            }

            nameField.setText("");
            weightField.setText("");
            refresh();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding component\n" + ex.getMessage());
        }
    }

    private void deleteSelectedComponent() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a component to delete.");
            return;
        }

        try {
            List<Map<String, Object>> rows = InstructorDAO.getGradeComponents(sectionId);
            int componentId = (int) rows.get(row).get("component_id");

            InstructorDAO.deleteGradeComponent(componentId);
            refresh();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete component\n" + ex.getMessage());
        }
    }
}
