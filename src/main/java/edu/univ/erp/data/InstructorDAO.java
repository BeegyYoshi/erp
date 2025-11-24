package edu.univ.erp.data;

import java.sql.*;
import java.util.*;

public class InstructorDAO {
    public static List<Map<String, Object>> fetchInstructorSections(int instructorId) throws SQLException {
        String sql = """
        SELECT 
            s.section_id,
            CONCAT(c.code, ' - ', c.title) AS course,
            s.day_time,
            s.room
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        WHERE s.instructor_id = ?
        ORDER BY c.code
    """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("section_id", rs.getInt("section_id"));
                    row.put("course", rs.getString("course"));  // 👈 This matches UI
                    row.put("day_time", rs.getString("day_time"));
                    row.put("room", rs.getString("room"));
                    list.add(row);
                }
            }
        }

        return list;
    }

    public static List<Map<String, Object>> getGradeComponents(int sectionId) throws SQLException {
        String sql = """
            SELECT component_id, component_name, weight
            FROM grade_components
            WHERE section_id = ?
            ORDER BY component_id
        """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("component_id", rs.getInt("component_id"));
                    row.put("component_name", rs.getString("component_name"));
                    row.put("weight", rs.getDouble("weight"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    // Total weight for a section (for enforcement)
    public static double getTotalWeight(int sectionId) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(weight), 0)
            FROM grade_components
            WHERE section_id = ?
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        }
    }

    // Add a grade component
    public static boolean addGradeComponent(int sectionId, String name, double weight) throws SQLException {

        // UI expects this rule: total weight cannot exceed 100
        double current = getTotalWeight(sectionId);
        if (current + weight > 100.0) {
            return false;  // UI will show a message
        }

        String sql = """
            INSERT INTO grade_components (section_id, component_name, weight)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.setString(2, name);
            ps.setDouble(3, weight);

            ps.executeUpdate();
            return true;
        }
    }

    // Delete a component
    public static boolean deleteGradeComponent(int componentId) throws SQLException {
        String sql = """
            DELETE FROM grade_components
            WHERE component_id = ?
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, componentId);
            return ps.executeUpdate() > 0;
        }
    }
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
                return "Unknown";
            }
        }
    }
    public static List<Map<String,Object>> getEnrolledStudents(int sectionId) throws SQLException {
        String sql = """
        SELECT e.enrollment_id, u.username AS student,
               e.student_id
        FROM enrollments e
        JOIN users_auth u ON e.student_id = u.user_id
        WHERE e.section_id = ? 
          AND e.status = 'enrolled'
    """;

        List<Map<String,Object>> list = new ArrayList<>();
        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("enrollment_id", rs.getInt("enrollment_id"));
                m.put("student", rs.getString("student"));
                list.add(m);
            }
        }
        return list;
    }
    public static void saveFinalGrade(int enrollmentId, double finalGrade, String letter) throws SQLException {
        String sql = """
        INSERT INTO grades (enrollment_id, final_grade, letter_grade)
        VALUES (?, ?, ?)
        ON DUPLICATE KEY UPDATE final_grade = VALUES(final_grade),
                                letter_grade = VALUES(letter_grade)
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDouble(2, finalGrade);
            ps.setString(3, letter);
            ps.executeUpdate();
        }
    }
}
