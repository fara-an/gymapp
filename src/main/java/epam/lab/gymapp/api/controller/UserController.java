package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.service.implementation.TokenBlacklistService;
import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Credentials credentials) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword()
        );

        authenticationManager.authenticate(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse("Login successful", jwtToken));

    }

    @PatchMapping("/{userName}")
    public ResponseEntity<?> toggleActiveStatus(@PathVariable("userName") String username) {
        userService.toggleActiveStatus(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Changed password successfully"));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Instant expiry = jwtService.extractExpiration(token);
        tokenBlacklistService.blacklistToken(token, expiry);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}
