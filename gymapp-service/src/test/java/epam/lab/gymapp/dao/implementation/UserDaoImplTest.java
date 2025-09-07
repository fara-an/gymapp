package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.model.UserProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {

    @Mock
    private CreateReadDao<UserProfile, Long> dao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private UserDaoImpl userDao;

    private UserProfile user;

    @BeforeEach
    void setUp() {
        user = new UserProfile();
        user.setUserName("testUser");
        user.setPassword("encodedOldPassword");


    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));

        UserProfile found = userDao.findByUsername("testUser");

        assertEquals("testUser", found.getUserName());
    }

    @Test
    void findByUsername_ShouldThrow_WhenNotExists() {
        when(dao.findByUsername("missingUser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userDao.findByUsername("missingUser"));
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenValidOldPassword() {
        when(dao.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPassword");

        userDao.changePassword("testUser", "oldPass", "newPass");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(session).merge(user);
    }

    @Test
    void changePassword_ShouldThrowInvalidCredentials_WhenOldPasswordDoesNotMatch() {
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "encodedOldPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userDao.changePassword("testUser", "wrongOld", "newPass"));
    }

    @Test
    void changePassword_ShouldThrowDaoException_WhenMergeFails() {
        when(dao.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPassword");

        doThrow(new RuntimeException("DB error")).when(session).merge(user);

        assertThrows(DaoException.class,
                () -> userDao.changePassword("testUser", "oldPass", "newPass"));
    }


    @Test
    void toggleActiveStatus_ShouldFlipActiveStatus_WhenUserExists() {
        when(dao.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        user.setIsActive(true);
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));

        userDao.toggleActiveStatus("testUser");

        assertFalse(user.getIsActive());
        verify(session).merge(user);
    }

    @Test
    void toggleActiveStatus_ShouldThrow_WhenUserNotFound() {

        when(dao.findByUsername("missingUser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userDao.toggleActiveStatus("missingUser"));
    }

    @Test
    void toggleActiveStatus_ShouldThrowDaoException_WhenMergeFails() {
        when(dao.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        user.setIsActive(false);
        when(dao.findByUsername("testUser")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("DB error")).when(session).merge(user);

        assertThrows(DaoException.class,
                () -> userDao.toggleActiveStatus("testUser"));
    }
}
