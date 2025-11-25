package edu.univ.erp.ui.student;

import edu.univ.erp.MainFrame;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.interfaces.Refreshable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class GradesPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private final int studentId;

    private JTable table;
    private DefaultTableModel model;

    public GradesPanel(MainFrame mainFrame, int studentId) {
        this.mainFrame = mainFrame;
        this.studentId = studentId;
        buildUI();
        refresh();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Grades", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Course Code", "Course Name", "Final Grade", "Letter"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton downloadBtn = new JButton("Download Transcript (PDF)");
        JButton backBtn = new JButton("Back");

        bottom.add(downloadBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen("student"));

        downloadBtn.addActionListener(e -> downloadTranscript());
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
