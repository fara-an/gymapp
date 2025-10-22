package epam.lab.gymapp.cucumber.component.steps;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.implementation.TokenBlacklistService;
import epam.lab.gymapp.service.interfaces.UserService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerComponentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private ResultActions resultActions;

    @When("I log in with username {string} and password {string}")
    public void iLog_in_with_username_and_password(String username, String password) throws Exception {
        Credentials credentials = new Credentials(username, password);

        if (password.equals("wrongpass")) {
            Mockito.doThrow(new BadCredentialsException("Bad credentials"))
                    .when(authenticationManager).authenticate(Mockito.any());
        } else {
            Mockito.when(userDetailsService.loadUserByUsername(username))
                    .thenReturn(new User(username, password, Collections.emptyList()));
            Mockito.when(jwtService.generateToken(Mockito.any()))
                    .thenReturn("mocked-jwt-token");
        }

        resultActions = mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)));
    }

    @Then("UserController response should contain a JWT token")
    public void usercontroller_response_should_contain_a_jwt_token() throws Exception {
        resultActions.andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @When("I toggle active status for user {string}")
    public void i_toggle_active_status_for_user(String username) throws Exception {
        if (username.equals("ghost")) {
            Mockito.doThrow(new EntityNotFoundException("Error occurred during database interaction"))
                    .when(userService).toggleActiveStatus(username);
        }
        resultActions = mockMvc.perform(patch("/users/" + username));
    }

    @When("I change the password for user {string} from {string} to {string}")
    public void i_change_the_password_for_user_from_to(String username, String oldPass, String newPass) throws Exception {
        PasswordChangeDto dto = new PasswordChangeDto(username, oldPass, newPass);

        if (oldPass.equals("wrongOld")) {
            Mockito.doThrow(new IllegalArgumentException("Old password is incorrect"))
                    .when(userService).changePassword(username, oldPass, newPass);
        }

        resultActions = mockMvc.perform(put("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
    }

    @When("I log out with token {string}")
    public void i_log_out_with_token(String token) throws Exception {
        if (token.equals("invalid-jwt-token")) {
            Mockito.doThrow(new IllegalArgumentException("Invalid token"))
                    .when(jwtService).extractExpiration(token);
        } else {
            Mockito.when(jwtService.extractExpiration(token)).thenReturn(Instant.now().plusSeconds(3600));
        }

        resultActions = mockMvc.perform(get("/users/logout")
                .header("Authorization", "Bearer " + token));
    }

    @Then("UserController response status should be {int}")
    public void usercontroller_response_status_should_be(Integer status) throws Exception {
        resultActions.andExpect(status().is(status));
    }

    @Then("UserController response should contain message {string}")
    public void usercontroller_response_should_contain_message(String message) throws Exception {
        resultActions.andExpect(content().string(containsString(message)));
    }
}
