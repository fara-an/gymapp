package epam.lab.gymapp.utils;

import java.util.function.Function;

public class UsernameGenerator {

    private static final String USERNAME_SEPARATOR = ".";

    public static String generateUsername(String firstName, String lastName, Function<String, Boolean> usernameExistsChecker) {
        String baseUsername = firstName + USERNAME_SEPARATOR + lastName;  
        String username = baseUsername;
        int suffix = 1;

        while (usernameExistsChecker.apply(username)) {
            username = baseUsername + suffix;
            suffix++;
        }
        return username;
    }
}
