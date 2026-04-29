package ek.dk.biludlejning.utility;

import java.security.MessageDigest;
import java.util.Base64;

public class HashGenerator {
    public static void main(String[] args) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        String pass1 = "password_placeholder";
        String hash1 = Base64.getEncoder().encodeToString(digest.digest(pass1.getBytes()));

        digest.reset();
        String pass2 = "test-password";
        String hash2 = Base64.getEncoder().encodeToString(digest.digest(pass2.getBytes()));

        System.out.println("password_placeholder -> " + hash1);
        System.out.println("test-password -> " + hash2);
    }
}

