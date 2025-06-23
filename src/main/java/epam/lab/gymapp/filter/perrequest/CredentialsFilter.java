package epam.lab.gymapp.filter.perrequest;

import epam.lab.gymapp.aspect.CredentialsContextHolder;
import epam.lab.gymapp.dto.Credentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CredentialsFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("trainee/login") || path.equals("trainer/login"); //skipping for login endpoint
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Credentials credentials = (Credentials) session.getAttribute("credentials");
                if (credentials != null) {
                    CredentialsContextHolder.setCredentials(credentials);
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            CredentialsContextHolder.clear();
        }
    }
}
