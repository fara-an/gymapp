//package epam.lab.gymapp.cucumber.config.steps;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import epam.lab.gymapp.dto.request.login.Credentials;
//import epam.lab.gymapp.dto.response.login.LoginResponse;
//import epam.lab.gymapp.service.implementation.TokenBlacklistService;
//import epam.lab.gymapp.service.interfaces.UserService;
//import epam.lab.gymapp.jwt.JwtService;
//import io.cucumber.java.en.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.time.Instant;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//public class UserControllerSteps {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean private UserService userService;
//    @MockitoBean private UserDetailsService userDetailsService;
//    @MockitoBean private AuthenticationManager authenticationManager;
//    @MockitoBean private JwtService jwtService;
//    @MockitoBean private TokenBlacklistService tokenBlacklistService;
//
//    private MvcResult mvcResult;
//    private String jwtToken;
//
//    // -------------------------
//    // LOGIN
//    // -------------------------
//    @Given("a user with username {string} and password {string}")
//    public void a_user_with_username_and_password(String username, String password) {
//        UserDetails userDetails = org.springframework.security.core.userdetails.User
//                .withUsername(username).password(password).roles("USER").build();
//
//        if ("wrongPass".equals(password)) {
//            doThrow(new BadCredentialsException("Bad credentials"))
//                    .when(authenticationManager).authenticate(any());
//        } else {
//            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
//            when(jwtService.generateToken(userDetails)).thenReturn("mockToken");
//        }
//    }
//
//    @When("I send a POST request to {string}")
//    public void i_send_a_post_request_to(String path) throws Exception {
//        Credentials credentials = new Credentials("john", "secret");
//        mvcResult = mockMvc.perform(post(path)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(credentials)))
//                .andReturn();
//    }
//
//    // -------------------------
//    // RESPONSE VALIDATION
//    // -------------------------
//    @Then("the response status should be {int}")
//    public void the_response_status_should_be(Integer status) throws Exception {
//        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(status);
//    }
//
//    @Then("the response should contain {string}")
//    public void the_response_should_contain(String text) throws Exception {
//        assertThat(mvcResult.getResponse().getContentAsString()).contains(text);
//    }
//
//    @Then("the response should contain a JWT token")
//    public void the_response_should_contain_a_jwt_token() throws Exception {
//        String body = mvcResult.getResponse().getContentAsString();
//        LoginResponse loginResponse = objectMapper.readValue(body, LoginResponse.class);
//        assertThat(loginResponse.getToken()).isEqualTo("mockToken");
//    }
//
//    // -------------------------
//    // TOGGLE ACTIVE STATUS
//    // -------------------------
//    @When("I send a PATCH request to {string}")
//    public void i_send_a_patch_request_to(String path) throws Exception {
//        if (path.contains("ghost")) {
//            doThrow(new RuntimeException("User not found"))
//                    .when(userService).toggleActiveStatus(any());
//        } else {
//            doNothing().when(userService).toggleActiveStatus(any());
//        }
//        mvcResult = mockMvc.perform(patch(path)).andReturn();
//    }
//
//    // -------------------------
//    // CHANGE PASSWORD
//    // -------------------------
//    @When("I send a PUT request to {string} with old password {string} and new password {string}")
//    public void i_send_a_put_request_to_with_old_password_and_new_password(String path, String oldPass, String newPass) throws Exception {
//        if ("badOld".equals(oldPass)) {
//            doThrow(new IllegalArgumentException("Invalid old password"))
//                    .when(userService).changePassword(any(), eq(oldPass), any());
//        } else {
//            doNothing().when(userService).changePassword(any(), any(), any());
//        }
//
//        String body = """
//            { "username": "john", "oldPassword": "%s", "newPassword": "%s" }
//            """.formatted(oldPass, newPass);
//
//        mvcResult = mockMvc.perform(put(path)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(body))
//                .andReturn();
//    }
//
//    // -------------------------
//    // LOGOUT
//    // -------------------------
//    @Given("a valid JWT token {string}")
//    public void a_valid_jwt_token(String token) {
//        this.jwtToken = token;
//        when(jwtService.extractExpiration(token)).thenReturn(Instant.now().plusSeconds(3600));
//        doNothing().when(tokenBlacklistService).blacklistToken(any(), any());
//    }
//
//    @Given("an invalid JWT token {string}")
//    public void an_invalid_jwt_token(String token) {
//        this.jwtToken = token;
//        doThrow(new RuntimeException("Invalid token"))
//                .when(jwtService).extractExpiration(token);
//    }
//
//    @When("I send a GET request to {string} with the token")
//    public void i_send_a_get_request_to_with_the_token(String path) throws Exception {
//        mvcResult = mockMvc.perform(get(path)
//                .header("Authorization", "Bearer " + jwtToken))
//                .andReturn();
//    }
//}
