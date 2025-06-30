package epam.lab.gymapp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameGeneratorTest {

    @Mock
    private Function<String, Boolean> usernameExistsChecker;

    @Test
    void generateUsername_ShouldGenerateBaseUsername_WhenUsernameNotExists() {

        when(usernameExistsChecker.apply(anyString())).thenReturn(false);
        String firstName = "John";
        String lastName = "Doe";
        String expectedUsername = "John.Doe";

        String result = UsernameGenerator.generateUsername(firstName, lastName, usernameExistsChecker);

        assertEquals(expectedUsername, result);
    }

    @Test
    void generateUsername_ShouldAppendSuffix_WhenUsernameExists() {

        when(usernameExistsChecker.apply("John.Doe")).thenReturn(true);
        when(usernameExistsChecker.apply("John.Doe1")).thenReturn(false);
        String firstName = "John";
        String lastName = "Doe";

        // When
        String result = UsernameGenerator.generateUsername(firstName, lastName, usernameExistsChecker);

        // Then
        assertEquals("John.Doe1", result);
    }

    @Test
    void generateUsername_ShouldIncrementSuffix_UntilFindAvailableUsername() {

        when(usernameExistsChecker.apply("John.Doe")).thenReturn(true);
        when(usernameExistsChecker.apply("John.Doe1")).thenReturn(true);
        when(usernameExistsChecker.apply("John.Doe2")).thenReturn(false);
        String firstName = "John";
        String lastName = "Doe";

        String result = UsernameGenerator.generateUsername(firstName, lastName, usernameExistsChecker);

        assertEquals("John.Doe2", result);
    }

    @Test
    void generateUsername_ShouldHandleEmptyNames() {
        when(usernameExistsChecker.apply(anyString())).thenReturn(false);
        String firstName = "";
        String lastName = "";
        String expectedUsername = ".";  // Empty strings with just the separator

        String result = UsernameGenerator.generateUsername(firstName, lastName, usernameExistsChecker);

        assertEquals(expectedUsername, result);
    }

    @Test
    void generateUsername_ShouldGenerateUniqueUsernames() {

        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("User.Name");
        existingUsernames.add("User.Name1");
        
        when(usernameExistsChecker.apply(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return existingUsernames.contains(username);
        });

        String result = UsernameGenerator.generateUsername("User", "Name", usernameExistsChecker);

        assertFalse(existingUsernames.contains(result));
        assertEquals("User.Name2", result);
    }
}
