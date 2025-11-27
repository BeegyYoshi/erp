package edu.univ.erp.ui.admin;

import edu.univ.erp.MainFrame;
import edu.univ.erp.interfaces.Refreshable;
import edu.univ.erp.data.BackupDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminBackupPanel extends JPanel implements Refreshable {

    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel model;

    private static final String BACKUP_DIR = "src/main/resources/backup";

    // ----- THEME -----
    private static final Color BG_DARK = new Color(30, 30, 30);
    private static final Color PANEL_DARK = new Color(45, 45, 45);
    private static final Color TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color ACCENT = Color.decode("#39AEA8");

    public AdminBackupPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
        refresh();
    }

    private void buildUI() {

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // ---------- TITLE ----------
        JLabel title = new JLabel("Database Backup Manager", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        // ---------- TABLE ----------
        model = new DefaultTableModel(new String[]{"Backup File"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setBackground(PANEL_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(ACCENT.darker());
        table.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JTableHeader header = table.getTableHeader();
        header.setBackground(ACCENT);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        // ---------- BUTTON BAR ----------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottom.setBackground(BG_DARK);

        JButton backupBtn  = styledButton("Create Backup");
        JButton restoreBtn = styledButton("Restore Selected");
        JButton deleteBtn  = styledButton("Delete Selected");
        JButton backBtn    = styledButton("Back");

        bottom.add(backupBtn);
        bottom.add(restoreBtn);
        bottom.add(deleteBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        backupBtn.addActionListener(e -> createBackup());
        restoreBtn.addActionListener(e -> restoreBackup());
        deleteBtn.addActionListener(e -> deleteBackup());
        backBtn.addActionListener(e -> mainFrame.showScreen("admin"));
    }

    // ---------- Styled Button ----------
    private JButton styledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
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

    // ---------- Refresh Backup List ----------
    @Override
    public void refresh() {
        model.setRowCount(0);
        List<String> backups = BackupDAO.listBackups();
        for (String b : backups)
            model.addRow(new Object[]{b});
    }

    // ---------- Create Backup ----------
    private void createBackup() {
        try {
            String path = BackupDAO.createBackup();
            JOptionPane.showMessageDialog(this, "Backup created:\n" + path);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Backup failed:\n" + ex.getMessage());
        }
    }

    // ---------- Restore Backup ----------
    private void restoreBackup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a backup to restore.");
            return;
        }

        String file = model.getValueAt(row, 0).toString();

        try {
            BackupDAO.restoreBackup(BACKUP_DIR + "/" + file);
            JOptionPane.showMessageDialog(this, "Restore successful!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Restore failed:\n" + ex.getMessage());
        }
    }

    // ---------- Delete Backup ----------
    private void deleteBackup() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a backup to delete.");
            return;
        }

        String filename = model.getValueAt(row, 0).toString();

        if (BackupDAO.deleteBackup(filename)) {
            JOptionPane.showMessageDialog(this, "Backup deleted.");
            refresh();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete backup.");
        }
    }
}
