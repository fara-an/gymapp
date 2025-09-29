package epam.lab.gymapp.cucumber.integration.steps;


import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.cucumber.TestContextJwt;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestContextJwt testContextJwt;


    Logger LOGGER = LoggerFactory.getLogger(UserControllerSteps.class);

    private String currentUsername;
    private String currentPassword;
    private ResponseEntity<String> response;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Before()
    public void login() throws Exception {
        Credentials creds = new Credentials("Emily.Brown", "pass789");
        ResponseEntity<String> resp = restTemplate.postForEntity(url("/users/login"), creds, String.class);
        LoginResponse loginResponse = objectMapper.readValue(resp.getBody(), LoginResponse.class);
        testContextJwt.setJwtToken(loginResponse.getToken());
    }

    @Before("@changePass")
    public void loginBeforeScenario() throws Exception {
        Credentials creds = new Credentials("Emily.Brown", "pass789");
        ResponseEntity<String> resp = restTemplate.postForEntity(url("/users/login"), creds, String.class);

        LoginResponse loginResponse = objectMapper.readValue(resp.getBody(), LoginResponse.class);
        testContextJwt.setJwtToken(loginResponse.getToken());
    }

    @Given("a user with username {string} and password {string}")
    public void a_user_with_username_and_password(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
    }

    @When("I send a POST request to {string}")
    public void i_send_a_post_request_to(String path) {
        Credentials credentials = new Credentials(currentUsername, currentPassword);
        response = restTemplate.postForEntity(url(path), credentials, String.class);
    }


    @Then("the response should contain a JWT token")
    public void the_response_should_contain_a_jwt_token() throws Exception {
        LoginResponse loginResponse = objectMapper.readValue(response.getBody(), LoginResponse.class);
        assertThat(loginResponse.getToken()).isNotBlank();
        this.testContextJwt.setJwtToken(loginResponse.getToken());
    }


    @When("I send a PATCH request to {string}")
    public void i_send_a_patch_request_to(String path) {
        LOGGER.debug("Returned jwt token is {}", testContextJwt.getJwtToken());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        response = restTemplate.exchange(url(path), HttpMethod.PATCH, entity, String.class);
    }

    @When("I send a PUT request to {string} with old password {string} and new password {string} of username {string}")
    public void i_send_a_put_request_to_with_old_password_and_new_password(String path, String oldPass, String newPass, String username) {
        LOGGER.debug("Returned jwt token is {}", testContextJwt.getJwtToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(testContextJwt.getJwtToken());

        this.currentUsername = username;
        PasswordChangeDto dto = new PasswordChangeDto(currentUsername, oldPass, newPass);
        HttpEntity<PasswordChangeDto> entity = new HttpEntity<>(dto, headers);

        response = restTemplate.exchange(url(path), HttpMethod.PUT, entity, String.class);
    }

    @Given("an invalid JWT token {string}")
    public void an_invalid_jwt_token(String token) {
        testContextJwt.setJwtToken(token);
    }

    @When("I send a GET request to {string} with the token")
    public void i_send_a_get_request_to_with_the_token(String path) {
        LOGGER.debug("Returned jwt token is {}", testContextJwt.getJwtToken());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange(url(path), HttpMethod.GET, entity, String.class);
    }
    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String text) {
        assertThat(response.getBody()).contains(text);
    }
}
