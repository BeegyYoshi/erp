package edu.univ.erp.data;

import java.sql.*;

public class SettingsDAO {

    public static boolean isMaintenanceOn() throws SQLException {
        String sql = "SELECT `value` FROM settings WHERE `key` = 'maintenance'";

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("value").equalsIgnoreCase("true");
            }
        }
        return false;
    }

    public static boolean setMaintenance(boolean on) throws SQLException {
        String sql = """
            INSERT INTO settings(`key`, `value`)
            VALUES ('maintenance', ?)
            ON DUPLICATE KEY UPDATE value = VALUES(value)
        """;

        try (Connection conn = ERPDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, on ? "true" : "false");
            ps.executeUpdate();
            return true;
        }
    }
}
