package com.example.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class GenerateTestSecrets {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java GenerateTestSecrets <file-path> [custom-password]");
            return;
        }

        String filePath = args[0];
        String customPassword = (args.length > 1) ? args[1] : null;

        File file = new File(filePath);

        if (file.exists()) {
            System.out.println("Test properties already exist at: " + filePath);
            return;
        }

        file.getParentFile().mkdirs();

        String username = "admin"; // Keeping username fixed for now
        String plainPassword = (customPassword != null) ? customPassword : generateRandomPassword(16);

        // Encode password using BCrypt
        String encodedPassword = "{bcrypt}" + new BCryptPasswordEncoder().encode(plainPassword);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("auth.username=" + username + "\n");
            writer.write("auth.password=" + encodedPassword + "\n");
            writer.write("auth.raw-password=" + plainPassword + "\n");
        }

        System.out.println("Generated " + filePath + " with encoded password.");
        if (customPassword != null) {
            System.out.println("Custom password used: " + plainPassword);
        } else {
            System.out.println("Plain password (only shown once): " + plainPassword);
        }
    }

    private static String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
