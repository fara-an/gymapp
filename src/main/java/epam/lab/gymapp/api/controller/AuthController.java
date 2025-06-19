package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Credentials credentials, HttpSession httpSession) {
        authenticationService.authenticateUser(credentials);
        httpSession.setAttribute("credentials", credentials);
        return ResponseEntity.ok("Login successful");
    }
}
