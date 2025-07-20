package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserDetailsService userDetailsService;

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




}
