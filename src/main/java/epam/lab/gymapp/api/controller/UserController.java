package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public UserController(UserService userService,  AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Credentials credentials) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword()
        );

        authenticationManager.authenticate(authentication);
        UserDetails userDetails = userService.loadUserByUsername(credentials.getUsername());
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


}
