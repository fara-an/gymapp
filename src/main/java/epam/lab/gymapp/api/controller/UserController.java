package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
                    schema = @Schema(implementation = MessageResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MessageResponse.class)
            ))
    })
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


    @Operation(
            summary = "Toggle user active status",
            description = "Flips the user’s <em>isActive</em> flag (activate ⇄ deactivate)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status toggled successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            )
    })
    @PatchMapping("/{userName}")
    public ResponseEntity<?> toggleActiveStatus(@PathVariable("userName") String username) {
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
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Old password is incorrect",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class)
                    )
            )
    })

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        userService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Changed password successfully"));
    }


}
