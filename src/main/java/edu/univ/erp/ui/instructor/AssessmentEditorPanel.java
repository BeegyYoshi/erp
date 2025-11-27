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
import java.util.List;
import java.util.Map;

public class AssessmentEditorPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int sectionId;

    private JTable table;
    private DefaultTableModel model;

    private JTextField nameField;
    private JTextField weightField;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AssessmentEditorPanel(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("Assessment Components", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Table Setup ----
        String[] cols = {"Component", "Weight (%)"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

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

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp, BorderLayout.CENTER);

        // ---- Bottom Input + Buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottom.setBackground(BG_DARK);

        nameField = styledTextField(12);
        weightField = styledTextField(5);

        JLabel nameLbl = styledLabel("Name:");
        JLabel weightLbl = styledLabel("Weight:");

        JButton addBtn = styledButton("Add");
        JButton deleteBtn = styledButton("Delete Selected");
        JButton backBtn = styledButton("Back");

        bottom.add(nameLbl);
        bottom.add(nameField);
        bottom.add(weightLbl);
        bottom.add(weightField);
        bottom.add(addBtn);
        bottom.add(deleteBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        // ---- Actions ----
        addBtn.addActionListener(e -> addComponent());
        deleteBtn.addActionListener(e -> deleteSelectedComponent());
        backBtn.addActionListener(e -> mainFrame.showScreen("section_dash_" + sectionId));
    }

    // ---- Styled TextField ----
    private JTextField styledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(PANEL_DARK);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(TEXT_LIGHT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return tf;
    }

    // ---- Styled Label ----
    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        return lbl;
    }

    // ---- Styled Button ----
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    // ---- Refresh Components ----
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
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load components\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---- Add Component ----
    private void addComponent() {

        // Maintenance mode check
        try {
            if (SettingsDAO.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this,
                        "Maintenance mode is ON — edits are disabled.",
                        "Blocked",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ignored) {}

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
                        "Cannot add: total weight exceeds 100%");
                return;
            }

            nameField.setText("");
            weightField.setText("");
            refresh();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding component\n" + ex.getMessage());
        }
    }

    // ---- Delete Component ----
    private void deleteSelectedComponent() {

        // Maintenance mode check
        try {
            if (SettingsDAO.isMaintenanceOn()) {
                JOptionPane.showMessageDialog(this,
                        "Maintenance mode is ON — edits are disabled.",
                        "Blocked",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ignored) {}

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a component to delete");
            return;
        }

        try {
            List<Map<String, Object>> rows = InstructorDAO.getGradeComponents(sectionId);
            int compId = (int) rows.get(row).get("component_id");

            InstructorDAO.deleteGradeComponent(compId);
            refresh();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete component\n" + ex.getMessage());
        }
    }
}
