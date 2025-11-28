package edu.univ.erp.data;

import java.sql.*;
import java.util.*;

public class AdminCourseDAO {

    public static List<Map<String, Object>> listAllCourses() throws SQLException {
        String sql = """
            SELECT course_id, code, title, credits
            FROM courses
            ORDER BY code
        """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("course_id", rs.getInt("course_id"));
                m.put("code", rs.getString("code"));
                m.put("title", rs.getString("title"));
                m.put("credits", rs.getInt("credits"));
                list.add(m);
            }
        }
        return list;
    }

    public static boolean insertCourse(String code, String title, int credits) throws SQLException {
        String sql = """
            INSERT INTO courses(code, title, credits)
            VALUES(?, ?, ?)
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.executeUpdate();
            return true;
        }
    }

    public static boolean updateCourse(int courseId, String code, String title, int credits) throws SQLException {
        String sql = """
            UPDATE courses
            SET code = ?, title = ?, credits = ?
            WHERE course_id = ?
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.setInt(4, courseId);

            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public static void addSampleCourses() {
        try {
            for (int i = 1; i <= 100; i++) {
                String code = "CS" + String.format("%03d", i);         // CS001, CS002 ...
                String title = "Sample Course " + i;                   // Sample Course 1 ...
                int credits = (i % 4) + 1;                            // 1–4 credits

                AdminCourseDAO.insertCourse(code, title, credits);
            }

            System.out.println("Inserted 100 sample courses successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed inserting sample courses: " + e.getMessage());
        }
    }

}
