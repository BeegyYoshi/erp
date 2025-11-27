package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class GradesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    // ---- Theme Colors ----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public GradesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---- Title ----
        JLabel title = new JLabel("My Grades", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ---- Table Setup ----
        String[] cols = {"Course Code", "Course Name", "Final Grade", "Letter"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // ---- Bottom Controls ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(BG_DARK);

        JButton downloadBtn = styledButton("Download Transcript (PDF)");
        JButton backBtn = styledButton("Back");

        bottom.add(downloadBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen("student"));

        downloadBtn.addActionListener(e -> downloadTranscript());
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

    @Override
    public void refresh() {
        model.setRowCount(0);

        try {
            List<Map<String,Object>> rows = GradeDAO.getGradesForStudent(studentId);

            for (Map<String,Object> row : rows) {
                model.addRow(new Object[]{
                        row.get("code"),
                        row.get("title"),
                        row.get("final_grade"),
                        row.get("letter_grade")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load grades:\n" + e.getMessage());
        }

        revalidate();
        repaint();
    }

    private void downloadTranscript() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("transcript.pdf"));

            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            String filePath = chooser.getSelectedFile().getAbsolutePath();

            GradeDAO.exportTranscriptPDF(studentId, filePath);

            JOptionPane.showMessageDialog(this, "Transcript downloaded!");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF export failed:\n" + ex.getMessage());
        }
    }
}
