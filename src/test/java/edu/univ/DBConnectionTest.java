package edu.univ.tests;

import edu.univ.erp.data.AuthDB;
import edu.univ.erp.data.ERPDB;

import org.junit.jupiter.api.Test;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @Test
    void testAuthDBConnection() throws Exception {
        try (Connection conn = AuthDB.getConnection()) {
            assertNotNull(conn, "AuthDB connection should not be null");
            assertTrue(conn.isValid(2), "AuthDB connection should be valid");
        }
    }

    @Test
    void testERPDBConnection() throws Exception {
        try (Connection conn = ERPDB.getConnection()) {
            assertNotNull(conn, "ERPDB connection should not be null");
            assertTrue(conn.isValid(2), "ERPDB connection should be valid");
        }
    }
}
