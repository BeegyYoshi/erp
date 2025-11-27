package edu.univ.tests;

import edu.univ.erp.auth.PasswordHasher;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    void testPasswordHashingAndVerification() {
        String pass = "secret123";
        String hash = PasswordHasher.hash(pass);

        assertNotNull(hash);
        assertTrue(PasswordHasher.verify(pass, hash));
        assertFalse(PasswordHasher.verify("wrongpass", hash));
    }
}
