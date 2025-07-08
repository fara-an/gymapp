package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.dto.error.ErrorResponse;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService<UserProfile, CreateReadDao<UserProfile, Long>> userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService<UserProfile, CreateReadDao<UserProfile, Long>> userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Log in",
            description = "Validates user credentials and stores them in the HTTP session on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request body(validation failed)", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Credentials credentials, HttpSession session) {
        performLogin(credentials.getUsername(), credentials.getPassword(), session);
        return ResponseEntity.ok("Login successful");

    }


    @Operation(
            summary = "Toggle trainee active status",
            description = "Flips the trainee’s <em>isActive</em> flag (activate ⇄ deactivate)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status toggled successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping("/toggleActiveStatus")
    public ResponseEntity<?> toggleActiveStatus(String username) {
        userService.toggleActiveStatus(username);
        return ResponseEntity.ok().build();
    }


    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed (e.g., blank new password)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Old password is incorrect",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok("Changed password successfully");
    }


    private void performLogin(String username, String password, HttpSession session) {
        Credentials credentials = new Credentials(username, password);
        authenticationService.authenticateUser(credentials);
        session.setAttribute("credentials", credentials);
    }
}
