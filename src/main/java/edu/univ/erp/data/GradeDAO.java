package edu.univ.erp.data;

import java.io.FileWriter;
import java.sql.*;
import java.util.*;

public class GradeDAO {

    /* --------------------------------------------------------
       1. Fetch all final grades for a section
    --------------------------------------------------------- */
    public static List<Double> getFinalGradesForSection(int sectionId) throws SQLException {

        String sql = """
        SELECT final_grade 
        FROM grades 
        WHERE section_id = ?
    """;

        List<Double> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getDouble("final_grade"));
                }
            }
        }
        return list;
    }

    /* --------------------------------------------------------
       2. Compute & save final grade for a student
    --------------------------------------------------------- */
    public static void computeAndSaveFinalGrade(int enrollmentId, int sectionId) throws SQLException {

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
                    total += rs.getDouble("score") * (rs.getDouble("weight") / 100.0);
                }
            }
        }

        String letter = convertToLetter(total);

        saveFinalGrade(enrollmentId, total, letter);
    }

    /* --------------------------------------------------------
       3. Save or Update final grade
    --------------------------------------------------------- */
    public static void saveFinalGrade(int enrollmentId, double finalGrade, String letter)
            throws SQLException {

        String sql = """
            INSERT INTO grades (enrollment_id, final_grade, letter_grade)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                final_grade = VALUES(final_grade),
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

    /* --------------------------------------------------------
       4. Aggregate Statistics
    --------------------------------------------------------- */

    public static double getClassAverage(int sectionId) throws SQLException {
        String sql = """
            SELECT AVG(g.final_grade)
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
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

    public static double getClassMin(int sectionId) throws SQLException {
        String sql = """
            SELECT MIN(g.final_grade)
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
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

    public static double getClassMax(int sectionId) throws SQLException {
        String sql = """
            SELECT MAX(g.final_grade)
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
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

    /* --------------------------------------------------------
       5. Grade distribution (A,B,C,D,F)
    --------------------------------------------------------- */
    public static Map<String,Integer> getGradeDistribution(int sectionId) throws SQLException {

        String sql = """
            SELECT letter_grade, COUNT(*) AS cnt
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.section_id = ?
            GROUP BY letter_grade
        """;

        Map<String,Integer> map = new HashMap<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("letter_grade"), rs.getInt("cnt"));
                }
            }
        }
        return map;
    }

    /* --------------------------------------------------------
       Helper
    --------------------------------------------------------- */
    private static String convertToLetter(double g) {
        if (g >= 85) return "A";
        if (g >= 75) return "B";
        if (g >= 65) return "C";
        if (g >= 50) return "D";
        return "F";
    }

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

    public static List<Map<String, Object>> getComponents(int sectionId) throws SQLException {
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

    /**
     * Returns a map of component_id -> score for a given enrollment.
     * If a component has no score entry yet, it simply won't be present in the map.
     */
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

    /**
     * Checks whether all components for the given section have scores for the given enrollment.
     */
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
     * Compute the weighted final grade for an enrollment in a section.
     * Throws IllegalStateException if not all component scores are present.
     *
     * Calculation: sum(score * weight/100.0) over all components.
     */
    public static double getFinalGrade(int enrollmentId, int sectionId) throws SQLException {
        // enforce completeness
        if (!areAllScoresPresent(enrollmentId, sectionId)) {
            throw new IllegalStateException("Not all component scores are present for this student.");
        }

        String sql = """
            SELECT gc.weight, gs.score
            FROM grade_components gc
            JOIN grade_scores gs ON gc.component_id = gs.component_id
            WHERE gc.section_id = ? AND gs.enrollment_id = ?
        """;

        double total = 0.0;
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

    /**
     * Convert numeric grade into a letter grade.
     * Modify thresholds if you want different mapping.
     */
    public static String getLetterGrade(double numeric) {
        if (numeric >= 85.0) return "A";
        if (numeric >= 75.0) return "B";
        if (numeric >= 65.0) return "C";
        if (numeric >= 50.0) return "D";
        return "F";
    }

    /**
     * Return a list of students (enrollments) for a section along with any existing final grade.
     * Each map contains:
     *  - enrollment_id (Integer)
     *  - student_id (Integer)
     *  - student (String)  // username from auth_db.users_auth
     *  - final_grade (Double) // nullable
     *  - letter_grade (String) // nullable
     *
     * NOTE: this queries auth_db.users_auth to fetch username. Adjust DB name if needed.
     */
    public static List<Map<String, Object>> getStudentsWithGrades(int sectionId) throws SQLException {
        // adjust auth DB name if different; using auth_db as example
        String sql = """
            SELECT e.enrollment_id,
                   e.student_id,
                   u.username AS student,
                   g.final_grade,
                   g.letter_grade
            FROM enrollments e
            LEFT JOIN auth_db.users_auth u ON u.user_id = e.student_id
            LEFT JOIN grades g ON g.enrollment_id = e.enrollment_id AND g.section_id = ?
            WHERE e.section_id = ? AND e.status = 'enrolled'
            ORDER BY u.username
        """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // first parameter belongs to the LEFT JOIN on grades, second to WHERE
            ps.setInt(1, sectionId);
            ps.setInt(2, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("enrollment_id", rs.getInt("enrollment_id"));
                    m.put("student_id", rs.getInt("student_id"));
                    m.put("student", rs.getString("student"));
                    Double fg = rs.getObject("final_grade") == null ? null : rs.getDouble("final_grade");
                    m.put("final_grade", fg);
                    m.put("letter_grade", rs.getString("letter_grade"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    /**
     * Save or update a final grade row in grades table for given enrollment and section.
     * Uses UNIQUE(enrollment_id, section_id) in grades to upsert.
     */
    public static void saveOrUpdateFinalGrade(int enrollmentId, int sectionId, double finalGrade, String letter)
            throws SQLException {
        String sql = """
            INSERT INTO grades (enrollment_id, section_id, final_grade, letter_grade)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                final_grade = VALUES(final_grade),
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

}
