package edu.univ.erp.data;

import java.sql.*;

public class EnrollmentDAO {

    // Register a student to a section
    public static boolean enrollStudent(int studentId, int sectionId) throws SQLException {
        String sql = """
            INSERT INTO enrollments (student_id, section_id)
            VALUES (?, ?)
            """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            ps.executeUpdate();
            return true;
        }
        catch (SQLException ex) {
            System.out.println("INSERT FAILED");
            System.out.println("SQL STATE: " + ex.getSQLState());
            System.out.println("ERROR CODE: " + ex.getErrorCode());
            // Error code 23000 = duplicate (unique key violation)
            if ("23000".equals(ex.getSQLState())) {
                return false;
            }
            throw ex;
        }
    }

    // Drop a registered course (change status)
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

    // Fetch all active enrollments for a student (for DropCoursesWindow)
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
}
