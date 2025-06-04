package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.services.serviceInterfaces.UsernamePasswordGeneration;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class DotSeparatedUsernameAndPasswordGeneration implements UsernamePasswordGeneration {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random random = new SecureRandom();


    @Override
    public String generateUsername(String firstName, String lastName, long numberOfSameUsernames) {
        String baseUsername = firstName + "." + lastName;
        String userName = baseUsername;
        if (numberOfSameUsernames >= 1) {
            userName = userName + numberOfSameUsernames;
        }
        return userName;
    }

    @Override
    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append((CHARS.charAt(random.nextInt(CHARS.length()))));

        }
        return password.toString();

    }
}
