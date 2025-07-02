package epam.lab.gymapp.filter.perrequest;

import epam.lab.gymapp.aspect.CredentialsContextHolder;
import epam.lab.gymapp.dto.request.login.Credentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CredentialsFilterTest {

    CredentialsFilter filter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpSession session;

    @Mock
    FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        filter = new CredentialsFilter();
        CredentialsContextHolder.clear();
    }

    @ParameterizedTest
    @ValueSource(strings = {"trainer/register", "trainee/register"})
    void shouldSkipFilteringForRegisterPaths(String path) throws ServletException {
        when(request.getServletPath()).thenReturn(path);
        boolean shouldNotFilter = filter.shouldNotFilter(request);
        assertTrue(shouldNotFilter, "Filter should skip " + path);
    }

    @Test
    void shouldSetCredentialsToContext_WhenSessionHasCredentials() throws ServletException, IOException {
        Credentials credentials = new Credentials("user", "token");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("credentials")).thenReturn(credentials);

        doAnswer(inv -> {
            assertEquals(credentials, CredentialsContextHolder.getCredentials());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(CredentialsContextHolder.getCredentials());
    }

    @Test
    void shouldNotSetCredentials_WhenSessionIsNull() throws Exception {

        when(request.getSession(false)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(CredentialsContextHolder.getCredentials());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearContextHolder_AfterFilterChain() throws Exception {
        Credentials credentials = new Credentials("user", "token");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("credentials")).thenReturn(credentials);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(CredentialsContextHolder.getCredentials(), "CredentialsContextHolder must be cleared after filter");
    }
}



