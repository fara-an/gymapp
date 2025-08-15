package epam.lab.gymapp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorTest {

    @Test
    void generatePassword_ShouldReturnPasswordOfCorrectLength() {
        String password = PasswordGenerator.generatePassword();
        
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_ShouldContainOnlyValidCharacters() {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        String password = PasswordGenerator.generatePassword();
        
        assertTrue(password.matches("^[A-Za-z0-9]+"));
        
        for (char c : password.toCharArray()) {
            assertTrue(validChars.contains(String.valueOf(c)));
        }
    }

    @Test
    void generatePassword_ShouldGenerateDifferentPasswords() {
        String password1 = PasswordGenerator.generatePassword();
        String password2 = PasswordGenerator.generatePassword();
        
        assertNotEquals(password1, password2, "Consecutive password generations should produce different results");
    }
}
