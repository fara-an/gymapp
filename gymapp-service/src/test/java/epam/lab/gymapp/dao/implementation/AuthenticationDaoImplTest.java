package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.model.UserProfile;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationDaoImplTest  {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<UserProfile> query;

    @InjectMocks
    AuthenticationDaoImpl underTest;

    private Credentials credentials;

    @BeforeEach
    void setUp() {
        credentials = new Credentials("Clementine.Krucszynski", "esosm");
    }


    @Test
    void validateCredentials_WithValidCredentials_ReturnsTrue() {
        UserProfile userProfile = UserProfile.builder().isActive(true).build();
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(AuthenticationDaoImpl.HQL_USERNAME_PASSWORD, UserProfile.class)).thenReturn(query);
        when(query.setParameter("username", credentials.getUsername())).thenReturn(query);
        when(query.setParameter("password", credentials.getPassword())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(userProfile);

        boolean result = underTest.validateCredentials(credentials);

        assertTrue(result);
        verify(query).uniqueResult();
    }

    @Test
    void validateCredentials_WithInvalidPassword_ReturnsFalse() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(AuthenticationDaoImpl.HQL_USERNAME_PASSWORD,UserProfile.class)).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        boolean result = underTest.validateCredentials(credentials);

        assertFalse(result);
        verify(query).uniqueResult();

    }

    @Test
   void validateCredentials_throwsDaoException_onHibernateError(){
        when(sessionFactory.getCurrentSession()).thenThrow(new HibernateException("Db down"));
        assertThrows(DaoException.class, ()-> underTest.validateCredentials(credentials));
    }
}
