package edu.univ.erp.data;

import java.sql.*;
import java.util.*;
import java.util.List;

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
                        Map<String, Object> m = new HashMap<>();
                        m.put("component_id", rs.getInt("component_id"));
                        m.put("component_name", rs.getString("component_name"));
                        m.put("weight", rs.getDouble("weight"));
                        list.add(m);
                    }
                }
            }
            return list;
        }

    /* --------------------------------------------------------
       2. Get enrolled students for a section
    --------------------------------------------------------- */
        public static List<Map<String,Object>> getEnrolledStudents(int sectionId) throws SQLException {

            String sql = """
            SELECT e.enrollment_id, u.username AS student
            FROM enrollments e
            JOIN auth_db.users_auth u ON u.user_id = e.student_id
            WHERE e.section_id = ?
              AND e.status = 'enrolled'
        """;

            List<Map<String,Object>> list = new ArrayList<>();

            try (Connection conn = ERPDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, sectionId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String,Object> m = new HashMap<>();
                        m.put("enrollment_id", rs.getInt("enrollment_id"));
                        m.put("student", rs.getString("student"));
                        list.add(m);
                    }
                }
            }
            return list;
        }

    /* --------------------------------------------------------
       3. Fetch component scores for a given enrollment
    --------------------------------------------------------- */
        public static Map<Integer, Double> getScoresForStudent(int enrollmentId) throws SQLException {

            String sql = """
            SELECT component_id, score
            FROM grade_scores
            WHERE enrollment_id = ?
        """;

            Map<Integer, Double> scores = new HashMap<>();

            try (Connection conn = ERPDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, enrollmentId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        scores.put(rs.getInt("component_id"), rs.getDouble("score"));
                    }
                }
            }
            return scores;
        }

    /* --------------------------------------------------------
       4. Insert OR update a score  (supports re-grading)
    --------------------------------------------------------- */
        public static void saveOrUpdateScore(int enrollmentId, int componentId, double score)
            throws SQLException {

            String sql = """
            INSERT INTO grade_scores (enrollment_id, component_id, score)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE score = VALUES(score)
        """;

            try (Connection conn = ERPDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, enrollmentId);
                ps.setInt(2, componentId);
                ps.setDouble(3, score);

                ps.executeUpdate();
            }
        }

        public static double computeFinalGrade(int enrollmentId, int sectionId) throws SQLException {

            String sql = """
            SELECT gc.weight, gs.score
            FROM grade_components gc
            JOIN grade_scores gs ON gc.component_id = gs.component_id
            WHERE gc.section_id = ? AND gs.enrollment_id = ?
        """;

            double total = 0;

            try (Connection conn = ERPDB.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, sectionId);
                ps.setInt(2, enrollmentId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        double weight = rs.getDouble("weight");
                        double score = rs.getDouble("score");
                        total += score * (weight / 100.0);
                    }
                }
            }
            return total;
        }

    /* --------------------------------------------------------
       7. Save final grade & letter
    --------------------------------------------------------- */
    public static void saveFinalGrade(int enrollmentId, int sectionId, double finalGrade, String letter)
            throws SQLException {

        String sql = """
        INSERT INTO grades (enrollment_id, section_id, final_grade, letter_grade)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE final_grade = VALUES(final_grade),
                                letter_grade = VALUES(letter_grade)
    """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, sectionId);
            ps.setDouble(3, finalGrade);
            ps.setString(4, letter);

            ps.executeUpdate();
        }
    }
}
