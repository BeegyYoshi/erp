package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordHasher;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AuthDAO {

    public static boolean changePassword(String username, String oldPass, String newPass) throws SQLException {

        String fetch = """
            SELECT user_id, password_hash
            FROM auth_db.users_auth
            WHERE username = ?
        """;

        try (Connection conn = AuthDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(fetch)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    // username not found
                    return false;
                }

                int userId = rs.getInt("user_id");
                String storedHash = rs.getString("password_hash");

                // Check old password
                if (!BCrypt.checkpw(oldPass, storedHash)) {
                    return false;
                }

                // Hash new password
                String newHash = PasswordHasher.hash(newPass);

                String update = """
                    UPDATE auth_db.users_auth
                    SET password_hash = ?
                    WHERE user_id = ?
                """;

                try (PreparedStatement ps2 = conn.prepareStatement(update)) {
                    ps2.setString(1, newHash);
                    ps2.setInt(2, userId);
                    ps2.executeUpdate();
                }

                return true;
            }
        }
    }
}
