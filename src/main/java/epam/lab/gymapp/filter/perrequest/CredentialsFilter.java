package epam.lab.gymapp.filter.perrequest;

import epam.lab.gymapp.aspect.CredentialsContextHolder;
import epam.lab.gymapp.dto.request.login.Credentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CredentialsFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String path = request.getServletPath();
        return path.equals("trainer/register") || path.equals("trainee/register"); //skipping for login endpoint
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                LOGGER.debug("Getting credentials from session");
                Credentials credentials = (Credentials) session.getAttribute("credentials");
                if (credentials != null) {
                    LOGGER.debug("Setting credentials to contextHolder");
                    CredentialsContextHolder.setCredentials(credentials);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            CredentialsContextHolder.clear();
        }
    }
}
