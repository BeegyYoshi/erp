package edu.univ.erp.data;

import java.sql.*;
import java.util.*;

public class AdminSectionDAO {

    public static List<Map<String,Object>> listAllSections() throws SQLException {
        String sql = """
            SELECT s.section_id, s.course_id, c.code, c.title,
                   s.instructor_id, u.username AS instructor,
                   s.day_time, s.room, s.capacity,
                   s.semester, s.year
            FROM sections s
            JOIN courses c ON s.course_id = c.course_id
            LEFT JOIN auth_db.users_auth u ON u.user_id = s.instructor_id
            ORDER BY c.code, s.section_id
        """;

        List<Map<String,Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("section_id", rs.getInt("section_id"));
                m.put("course_id", rs.getInt("course_id"));
                m.put("code", rs.getString("code"));
                m.put("title", rs.getString("title"));
                m.put("instructor_id", rs.getObject("instructor_id"));
                m.put("instructor", rs.getString("instructor"));
                m.put("day_time", rs.getString("day_time"));
                m.put("room", rs.getString("room"));
                m.put("capacity", rs.getInt("capacity"));
                m.put("semester", rs.getString("semester"));
                m.put("year", rs.getInt("year"));
                list.add(m);
            }
        }
        return list;
    }

    public static boolean insertSection(
            int courseId, Integer instructorId, String dayTime,
            String room, int capacity, String semester, int year
    ) throws SQLException {

        String sql = """
            INSERT INTO sections(course_id, instructor_id, day_time, room, capacity, semester, year)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            if (instructorId == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, instructorId);
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);

            ps.executeUpdate();
            return true;
        }
    }

    public static boolean updateSection(
            int sectionId, int courseId, Integer instructorId, String dayTime,
            String room, int capacity, String semester, int year
    ) throws SQLException {

        String sql = """
            UPDATE sections
            SET course_id = ?, instructor_id = ?, day_time = ?, room = ?,
                capacity = ?, semester = ?, year = ?
            WHERE section_id = ?
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            if (instructorId == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, instructorId);
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);
            ps.setInt(8, sectionId);

            return ps.executeUpdate() > 0;
        }
    }

    public static boolean deleteSection(int sectionId) throws SQLException {
        // enforce: cannot delete if enrollments exist
        String check = "SELECT COUNT(*) FROM enrollments WHERE section_id = ?";
        String del = "DELETE FROM sections WHERE section_id = ?";

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return false;
                }
            }
        }

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(del)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean assignInstructor(int sectionId, int instructorId) throws SQLException {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;
        }
    }
}
