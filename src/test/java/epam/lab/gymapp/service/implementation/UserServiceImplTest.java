package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void testChangePassword_DelegatesToDao() {
        String username = "john.doe";
        String oldPassword = "old123";
        String newPassword = "new123";

        userService.changePassword(username, oldPassword, newPassword);

        verify(userDao).changePassword(username, oldPassword, newPassword);
    }

    @Test
    void testToggleActiveStatus_DelegatesToDao() {
        String username = "john.doe";

        userService.toggleActiveStatus(username);

        verify(userDao).toggleActiveStatus(username);
    }

    @Test
    void testFindByUsername_ReturnsCorrectUser() {
        String username = "john.doe";
        UserProfile expectedUser = new UserProfile();
        expectedUser.setUserName(username);

        when(userDao.findByUsername(username)).thenReturn(expectedUser);

        UserProfile actualUser = userService.findByUsername(username);

        assertEquals(expectedUser, actualUser);
        verify(userDao).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_ReturnsUserDetails() {
        String username = "john.doe";
        String password = "pass123";

        UserProfile userProfile = new UserProfile();
        userProfile.setUserName(username);
        userProfile.setPassword(password);

        when(userDao.findByUsername(username)).thenReturn(userProfile);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_ThrowsException_WhenUserNotFound() {
        String username = "nonexistent";

        when(userDao.findByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }

}
