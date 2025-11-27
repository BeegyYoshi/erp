package edu.univ.erp.ui.instructor;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.InstructorDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MySectionsPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int instructorId;

    private JTable table;
    private DefaultTableModel model;
    private List<Map<String, Object>> sections;

    // ---- Theme ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public MySectionsPanel(MainFrame mainFrame, int instructorId) {
        this.mainFrame = mainFrame;
        this.instructorId = instructorId;

        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("My Sections", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Table Setup ----
        String[] cols = {"Course", "Day/Time", "Room"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Bar ----
        JButton back = styledButton("Back");
        back.addActionListener(e -> mainFrame.showScreen("instructor"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);
        bottom.add(back);

        add(bottom, BorderLayout.SOUTH);

        // ---- Double-Click Event ----
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int sectionId = (int) sections.get(row).get("section_id");
                    openSectionDashboard(sectionId);
                }
            }
        });
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

    // ---- Refresh Table ----
    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            sections = InstructorDAO.fetchInstructorSections(instructorId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load sections\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        for (Map<String, Object> s : sections) {
            model.addRow(new Object[]{
                    s.get("course"),
                    s.get("day_time"),
                    s.get("room")
            });
        }

        revalidate();
        repaint();
    }

    // ---- Open Section Dashboard ----
    private void openSectionDashboard(int sectionId) {
        SectionDashboardPanel p = new SectionDashboardPanel(mainFrame, instructorId, sectionId);
        mainFrame.loadPanel("section_dash_" + sectionId, p);
        mainFrame.showScreen("section_dash_" + sectionId);
    }
}
