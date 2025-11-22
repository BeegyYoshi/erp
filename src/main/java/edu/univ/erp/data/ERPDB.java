// src/main/java/edu/univ/erp/data/ERPDB.java
package edu.univ.erp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ERPDB {
    private static final String URL = "jdbc:mariadb://localhost:3306/erp_db";
    private static final String USER = "root";
    private static final String PASS = "password123";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
