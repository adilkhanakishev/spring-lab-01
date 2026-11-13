package kz.iitu.springlab.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Task 2.2 Helper Utility to demonstrate BCrypt hash generation with random salts.
 */
public class BCryptGenerator {

    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();

        System.out.println("=== Task 2.2: BCrypt Password Hash Generation ===");
        System.out.println("Password 'user123' (Run 1):  " + encoder.encode("user123"));
        System.out.println("Password 'user123' (Run 2):  " + encoder.encode("user123"));
        System.out.println("Password 'admin123' (Run 1): " + encoder.encode("admin123"));
        System.out.println("Password 'admin123' (Run 2): " + encoder.encode("admin123"));
        System.out.println("==================================================");
    }
}
