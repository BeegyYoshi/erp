package edu.univ.erp.data;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

public class GradeDAO {



    public static Map<String, Object> getClassStatistics(int sectionId) throws SQLException {
        String sql = """
        SELECT 
            COUNT(*) AS total,
            AVG(final_grade) AS average,
            MAX(final_grade) AS highest,
            MIN(final_grade) AS lowest,
            100 * SUM(CASE WHEN final_grade >= 50 THEN 1 ELSE 0 END) / COUNT(*) AS pass_percentage
        FROM grades g
        JOIN enrollments e ON g.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
    """;

        Map<String, Object> map = new HashMap<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    map.put("total", rs.getInt("total"));
                    map.put("average", rs.getDouble("average"));
                    map.put("highest", rs.getDouble("highest"));
                    map.put("lowest", rs.getDouble("lowest"));
                    map.put("pass_percentage", rs.getDouble("pass_percentage"));
                }
            }
        }
        return map;
    }

    public static void exportSectionGradesToCSV(int sectionId, String path) throws Exception {
        String sql = """
        SELECT 
            u.username AS student,
            gc.component_name,
            gs.score,
            g.final_grade,
            g.letter_grade
        FROM enrollments e
        JOIN auth_db.users_auth u ON u.user_id = e.student_id
        LEFT JOIN grade_scores gs ON gs.enrollment_id = e.enrollment_id
        LEFT JOIN grade_components gc ON gc.component_id = gs.component_id
        LEFT JOIN grades g ON g.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
        ORDER BY student, gc.component_id
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery();
                 FileWriter fw = new FileWriter(path)) {

                fw.write("Student,Component,Score,Final Grade,Letter Grade\n");

                while (rs.next()) {
                    fw.write(
                            rs.getString("student") + "," +
                                    rs.getString("component_name") + "," +
                                    rs.getString("score") + "," +
                                    rs.getString("final_grade") + "," +
                                    rs.getString("letter_grade") + "\n"
                    );
                }
            }
        }
    }

    public static boolean areAllScoresPresent(int enrollmentId, int sectionId) throws SQLException {
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM grade_components WHERE section_id = ?) AS total_components,
                (SELECT COUNT(*) FROM grade_scores gs
                 JOIN grade_components gc ON gs.component_id = gc.component_id
                 WHERE gs.enrollment_id = ? AND gc.section_id = ?) AS scored
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.setInt(2, enrollmentId);
            ps.setInt(3, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_components");
                    int scored = rs.getInt("scored");
                    return total > 0 && total == scored;
                }
            }
        }
        return false;
    }

    /**
     * Save or update a final grade row in grades table for given enrollment and section.
     * Uses UNIQUE(enrollment_id, section_id) in grades to upsert.
     */
    public static String getCourseCodeForSection(int sectionId) throws SQLException {
        String sql = """
        SELECT c.code
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        WHERE s.section_id = ?
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("code");
                }
            }
        }

        return "UNKNOWN";
    }
    public static List<Map<String, Object>> getGradesForStudent(int studentId) throws SQLException {

        String sql = """
        SELECT 
            g.final_grade, g.letter_grade,
            c.code, c.title
        FROM grades g
        JOIN enrollments e ON g.enrollment_id = e.enrollment_id
        JOIN sections s ON e.section_id = s.section_id
        JOIN courses c ON s.course_id = c.course_id
        WHERE e.student_id = ?
    """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("code", rs.getString("code"));
                    map.put("title", rs.getString("title"));
                    map.put("final_grade", rs.getDouble("final_grade"));
                    map.put("letter_grade", rs.getString("letter_grade"));
                    list.add(map);
                }
            }
        }

        return list;
    }
    public static String getRollNo(int studentId) throws SQLException {

        String sql = """
        SELECT roll_no
        FROM students
        WHERE user_id = ?
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("roll_no");
            }
        }

        return "UNKNOWN";
    }
    public static void exportTranscriptPDF(int studentId, String filePath) throws Exception {

        // ---------- Fetch student roll number ----------
        String rollNo = GradeDAO.getRollNo(studentId); // implement below

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        document.add(new Paragraph("Student Transcript"));
        document.add(new Paragraph("Roll Number: " + rollNo));
        document.add(new Paragraph("\n"));

        List<Map<String, Object>> grades = GradeDAO.getGradesForStudent(studentId);

        if (grades.isEmpty()) {
            document.add(new Paragraph("No grades available."));
            document.close();
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.addCell("Course Code");
        table.addCell("Course Name");
        table.addCell("Total Grade");
        table.addCell("Letter");

        for (Map<String, Object> row : grades) {

            table.addCell(String.valueOf(row.getOrDefault("code", "")));
            table.addCell(String.valueOf(row.getOrDefault("title", "")));
            table.addCell(String.valueOf(row.getOrDefault("final_grade", "")));
            table.addCell(String.valueOf(row.getOrDefault("letter_grade", "")));
        }

        document.add(table);
        document.close();
    }
}
