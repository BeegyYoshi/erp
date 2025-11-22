package edu.univ.erp.service;

import edu.univ.erp.data.CourseDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class StudentService {

    /**
     * Returns a list of maps; each map is a row with keys:
     * course_id, code, title, credits, section_id, instructor_name, day_time, room, capacity, semester, year
     */
    public static List<Map<String, Object>> getCourseCatalog() throws SQLException {
        return CourseDAO.fetchCourseCatalog();
    }
}
