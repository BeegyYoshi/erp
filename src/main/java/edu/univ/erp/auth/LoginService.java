package edu.univ.erp.auth;

import edu.univ.erp.data.AuthDB;

import java.sql.*;

public class LoginService {

    public static LoginResult login(String username, String password) {
        try (Connection conn = AuthDB.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id, role, password_hash FROM users_auth WHERE username=?"
            );
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return LoginResult.error("Unknown username");
            }

            String hash = rs.getString("password_hash");

            if (!PasswordHasher.verify(password, hash)) {
                return LoginResult.error("Incorrect password");
            }

            int userId = rs.getInt("user_id");
            String role = rs.getString("role");

            return LoginResult.success(userId, role);

        } catch (Exception e) {
            e.printStackTrace();
            return LoginResult.error("System error");
        }
    }
}
