package edu.univ.erp.data;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class EnrollmentDAO {

    // Register a student to a section
    public static boolean enrollStudent(int studentId, int sectionId) throws SQLException {

        String checkCapacitySQL = """
        SELECT capacity
        FROM sections
        WHERE section_id = ?
        FOR UPDATE
    """;

        String enrollSQL = """
        INSERT INTO enrollments (student_id, section_id)
        VALUES (?, ?)
    """;

        String reduceCapacitySQL = """
        UPDATE sections
        SET capacity = capacity - 1
        WHERE section_id = ?
    """;

        try (Connection conn = ERPDB.getConnection()) {

            // IMPORTANT: Start transaction
            conn.setAutoCommit(false);

            // 1️⃣ Check capacity
            int capacity = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkCapacitySQL)) {
                ps.setInt(1, sectionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        capacity = rs.getInt("capacity");
                    } else {
                        conn.rollback();
                        throw new SQLException("Section not found: " + sectionId);
                    }
                }
            }

            if (capacity <= 0) {
                conn.rollback();
                throw new SQLException("Section is full. Capacity = 0.");
            }

            // 2️⃣ Try enrolling the student
            try (PreparedStatement ps = conn.prepareStatement(enrollSQL)) {
                ps.setInt(1, studentId);
                ps.setInt(2, sectionId);

                ps.executeUpdate();  // may throw duplicate error
            } catch (SQLException ex) {
                conn.rollback();

                if ("23000".equals(ex.getSQLState())) {
                    // Unique constraint violation: student already enrolled
                    return false;
                }

                throw ex;
            }

            // 3️⃣ Reduce capacity by 1
            try (PreparedStatement ps = conn.prepareStatement(reduceCapacitySQL)) {
                ps.setInt(1, sectionId);
                ps.executeUpdate();
            }

            // 4️⃣ Commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            throw e;
        }
    }

    // Drop a registered course (change status)
    public static boolean dropEnrollment(int studentId, int sectionId) throws SQLException {
        String sql = """
        DELETE FROM enrollments
        WHERE student_id = ? AND section_id = ?
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            int deleted = ps.executeUpdate();
            return deleted > 0;  // true if a row was removed
        }
    }
/*
    public static boolean dropEnrollment(int studentId, int sectionId) throws SQLException {
        String sql = """
            UPDATE enrollments
            SET status = 'dropped'
            WHERE student_id = ? AND section_id = ?
            """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            int updated = ps.executeUpdate();
            return updated > 0; // returns false if no row found
        }
    }
*/
    public static ResultSet fetchActiveEnrollments(int studentId) throws SQLException {
        String sql = """
            SELECT e.section_id, c.code, c.title
            FROM enrollments e
            JOIN sections s ON e.section_id = s.section_id
            JOIN courses c ON s.course_id = c.course_id
            WHERE e.student_id = ? AND e.status = 'enrolled'
            """;

        Connection conn = ERPDB.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, studentId);
        return ps.executeQuery(); // caller must close
    }
    public static List<Map<String, Object>> fetchTimetable(int studentId) throws SQLException {
        String sql = """
        SELECT 
            s.section_id,
            c.code,
            c.title,
            s.day_time,
            s.room
        FROM enrollments e
        JOIN sections s ON e.section_id = s.section_id
        JOIN courses c ON s.course_id = c.course_id
        WHERE e.student_id = ?
          AND e.status = 'enrolled'
        ORDER BY s.day_time
    """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("section_id", rs.getInt("section_id"));
                    m.put("code", rs.getString("code"));
                    m.put("title", rs.getString("title"));
                    m.put("day_time", rs.getString("day_time"));
                    m.put("room", rs.getString("room"));
                    list.add(m);
                }
            }
        }
        return list;
    }
}
