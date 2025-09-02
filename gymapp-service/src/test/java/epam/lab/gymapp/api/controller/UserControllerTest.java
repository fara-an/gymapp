package epam.lab.gymapp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.GymApplication;
import epam.lab.gymapp.config.metric.MetricsConfig;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.implementation.TokenBlacklistService;
import epam.lab.gymapp.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@Import(NoSecurityConfig.class)
@ImportAutoConfiguration(exclude = MetricsConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_ShouldReturn200AndToken() throws Exception {

        String username = "johndoe";
        String password = "password123";
        String token = "mocked-jwt-token";

        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);

        UserDetails userDetails = User.withUsername(username)
                .password(password)
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value(token));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).generateToken(userDetails);
    }
    @Test
    void toggleActiveStatus_ShouldReturn200() throws Exception {

        String username = "johndoe";

        doNothing().when(userService).toggleActiveStatus(username);

        mockMvc.perform(patch("/users/{userName}", username))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userService).toggleActiveStatus(username);
    }
    @Test
    void changePassword_ShouldReturn200AndSuccessMessage() throws Exception {

        PasswordChangeDto dto = PasswordChangeDto.builder()
                .username("johndoe")
                .oldPassword("oldPass123")
                .newPassword("newPass456")
                .build();


        doNothing().when(userService).changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());

        mockMvc.perform(put("/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Changed password successfully"));

        verify(userService).changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
    }

    @Test
    void logout_ShouldReturn200AndSuccessMessage() throws Exception {
        String token = "mocked-jwt-token";
        Instant expiry = Instant.now().plusSeconds(3600);

        when(jwtService.extractExpiration(token)).thenReturn(expiry);

        mockMvc.perform(get("/users/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(jwtService).extractExpiration(token);
        verify(tokenBlacklistService).blacklistToken(token, expiry);
    }


}
