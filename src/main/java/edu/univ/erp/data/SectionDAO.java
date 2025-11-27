package edu.univ.erp.data;

import java.sql.*;
import java.util.*;

public class SectionDAO {
    public static List<Map<String, Object>> fetchAvailableSections(int studentId) throws SQLException {

        String sql = """
        SELECT 
            s.section_id,
            c.code,
            c.title,
            s.day_time,
            s.room,
            s.capacity,
            u.username AS instructor
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        JOIN auth_db.users_auth u ON s.instructor_id = u.user_id
        WHERE s.capacity > 0
          AND NOT EXISTS (
              SELECT 1 FROM enrollments e
              WHERE e.section_id = s.section_id
                AND e.student_id = ?
          )
        ORDER BY c.code, s.section_id;
        """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("section_id", rs.getInt("section_id"));
                    row.put("code", rs.getString("code"));
                    row.put("title", rs.getString("title"));
                    row.put("day_time", rs.getString("day_time"));
                    row.put("room", rs.getString("room"));
                    row.put("capacity", rs.getInt("capacity"));
                    row.put("instructor", rs.getString("instructor"));
                    list.add(row);
                }
            }
        }

        return list;
    }
}
