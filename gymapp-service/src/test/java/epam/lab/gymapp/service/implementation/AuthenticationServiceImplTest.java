package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.AuthenticationDao;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    AuthenticationDao authenticationDao;

    @InjectMocks
    AuthenticationServiceImpl underTest;


    @Test
    void authenticateUser_success(){
        Credentials credentials = new Credentials("user", "pass");
        when(authenticationDao.validateCredentials(credentials)).thenReturn(true);

        assertDoesNotThrow(()-> underTest.authenticateUser(credentials));
        verify(authenticationDao).validateCredentials(credentials);
    }

    @Test
    void  authenticateUser_ShouldThrow_WhenCredentialsAreInvalid(){
        Credentials credentials = new Credentials("user", "pass");
        when(authenticationDao.validateCredentials(credentials)).thenReturn(false);

       assertThrows(InvalidCredentialsException.class, ()->underTest.authenticateUser(credentials));
       verify(authenticationDao).validateCredentials(credentials);
    }


}
