package epam.lab.gymapp.filter.perrequest;

import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component("authFilterBean")
public class AuthFilter extends OncePerRequestFilter {

    AuthenticationService authenticationService;

    public AuthFilter(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/trainers") || path.equals("/trainees") || path.equals("/users/login");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Inside AuthFilter");
        HttpSession session = request.getSession(false);
        if (session == null) {
            sendResponse(response, "Session is not set, can not authenticate user");
            return;
        }
        Credentials credentials = (Credentials) session.getAttribute("credentials");
        if (credentials == null) {
            sendResponse(response, "Credentials missing");
            return;
        }
        authenticationService.authenticateUser(credentials);
        filterChain.doFilter(request, response);
    }


    private void sendResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();

    }
}
