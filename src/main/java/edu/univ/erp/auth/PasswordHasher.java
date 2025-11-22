package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    // Create hashed password
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verify password
    public static boolean verify(String plainPassword, String hashedPassword) {
        boolean value = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("Stored hash: " + hashedPassword);
        System.out.println("hashed hash: " + PasswordHasher.hash(plainPassword));
        System.out.println("Password verification: " + value);
        return value;
    }
}
