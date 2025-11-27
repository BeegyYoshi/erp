package edu.univ.erp.data;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackupDAO {

    private static final String BACKUP_DIR = "src/main/resources/backup";

    static {
        File f = new File(BACKUP_DIR);
        if (!f.exists()) f.mkdirs();
    }

    // -------------------------------------------------------------------
    // Create Backup (writes .sql file in BACKUP_DIR)
    // -------------------------------------------------------------------
    public static String createBackup() throws Exception {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = BACKUP_DIR + "/backup_" + timestamp + ".sql";

        // run both databases dump
        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump", "-u", "appuser", "-ppassword123",
                "--databases", "auth_db", "erp_db"
        );
        pb.redirectOutput(new File(filename));

        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new Exception("mysqldump failed.");
        }

        return filename;
    }

    // -------------------------------------------------------------------
    // Restore Backup
    // -------------------------------------------------------------------
    public static void restoreBackup(String filePath) throws Exception {

        File f = new File(filePath);
        if (!f.exists())
            throw new FileNotFoundException("Backup file not found: " + filePath);

        ProcessBuilder pb = new ProcessBuilder(
                "mysql", "-u", "appuser", "-ppassword123"
        );
        pb.redirectInput(f);

        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new Exception("Restore failed.");
        }
    }

    // -------------------------------------------------------------------
    // List all .sql backups
    // -------------------------------------------------------------------
    public static List<String> listBackups() {
        File folder = new File(BACKUP_DIR);

        String[] files = folder.list((dir, name) -> name.endsWith(".sql"));

        if (files == null) return new ArrayList<>();

        Arrays.sort(files);
        return Arrays.asList(files);
    }

    // -------------------------------------------------------------------
    // Delete Backup File
    // -------------------------------------------------------------------
    public static boolean deleteBackup(String filename) {
        File f = new File(BACKUP_DIR, filename);
        return f.exists() && f.delete();
    }
}
