package edu.univ.erp.data;

import java.sql.*;

public class AuthDB {

    private static final String URL = "jdbc:mariadb://localhost:3306/auth_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "password123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
