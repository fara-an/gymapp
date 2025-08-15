package epam.lab.gymapp.utils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new SecureRandom();


    public static String generatePassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append((CHARS.charAt(random.nextInt(CHARS.length()))));

        }
        return password.toString();
    }
}
