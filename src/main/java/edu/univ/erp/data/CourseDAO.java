package edu.univ.erp.data;

import java.sql.*;
import java.util.*;

public class CourseDAO {

    // Use your erp_db connection; create ERPDB class if not exists, similar to AuthDB.
    // Example assumes a class edu.univ.erp.data.ERPDB.getConnection() that returns a Connection to erp_db.
    public static List<Map<String, Object>> fetchCourseCatalog() throws SQLException {
        String sql =
            "SELECT c.course_id, c.code, c.title, c.credits, " +
            "       s.section_id, CONCAT(u.username) AS instructor_name, s.day_time, s.room, s.capacity, s.semester, s.year " +
            "FROM erp_db.courses c " +
            "LEFT JOIN erp_db.sections s ON c.course_id = s.course_id " +
            "LEFT JOIN auth_db.users_auth u ON s.instructor_id = u.user_id " +
            "ORDER BY c.code, s.section_id";

        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> r = new HashMap<>();
                r.put("course_id", rs.getInt("course_id"));
                r.put("code", rs.getString("code"));
                r.put("title", rs.getString("title"));
                r.put("credits", rs.getInt("credits"));
                r.put("section_id", rs.getObject("section_id")); // could be null
                r.put("instructor_name", rs.getString("instructor_name"));
                r.put("day_time", rs.getString("day_time"));
                r.put("room", rs.getString("room"));
                r.put("capacity", rs.getObject("capacity"));
                r.put("semester", rs.getString("semester"));
                r.put("year", rs.getObject("year"));
                rows.add(r);
            }
        }
        return rows;
    }
}
