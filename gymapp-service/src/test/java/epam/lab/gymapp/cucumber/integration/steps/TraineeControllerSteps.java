package epam.lab.gymapp.cucumber.integration.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.cucumber.TestContextJwt;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TraineeControllerSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TestContextJwt testContextJwt;

    private HttpHeaders httpHeaders;

    private ResponseEntity<String> response;


    @Before
    public void login() throws JsonProcessingException {
        Credentials credentials = new Credentials("Emily.Brown", "pass789");
        ResponseEntity<String> responseBody = testRestTemplate.postForEntity("http://localhost:" + port + "/users/login", credentials, String.class);
        LoginResponse loginResponse = objectMapper.readValue(responseBody.getBody(), LoginResponse.class);
        testContextJwt.setJwtToken(loginResponse.getToken());
        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(testContextJwt.getJwtToken());


    }


    @When("I register a trainee with firstName {string}, lastName {string}, dateOfBirth {string} and address {string}")
    public void registerTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        TraineeRegistrationBody registrationBody = TraineeRegistrationBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDateTime.parse(dateOfBirth))
                .address(address)
                .build();

        HttpEntity<?> httpEntity = new HttpEntity<>(registrationBody, httpHeaders);
        response = testRestTemplate.postForEntity("/trainees", httpEntity, String.class);

    }

    @Given("a trainee with username {string} already exists")
    public void traineeAlreadyExists(String username) {
    }

    @When("I try to register a trainee with firstName {string}, lastName {string}, dateOfBirth {string} and address {string}")
    public void registerDuplicate(String firstName, String lastName, String dob, String address) {
        TraineeRegistrationBody body = TraineeRegistrationBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDateTime.parse(dob))
                .address(address)
                .build();
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        response = testRestTemplate.postForEntity("/trainees", httpEntity, String.class);
    }

    @When("I get trainee by username {string}")
    public void getTrainee(String username) {
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        response = testRestTemplate.exchange("/trainees/" + username, HttpMethod.GET,httpEntity, String.class);
    }

    @Then("I should receive trainee details with name {string}")
    public void traineeDetails(String name) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(name);
    }


    @Then("Trainee Controller: the response status should be {int}")
    public void responseStatusShouldBe(int status) {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
    }

    @Then("Trainee Controller: the response should contain username and password")
    public void responseContainsUsernameAndPassword() {
        assertThat(response.getBody()).contains("username").contains("password");
    }

    @When("I get trainings for trainee {string}")
    public void getTrainings(String username) {
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        response = testRestTemplate.exchange("/trainees/" + username + "/trainings",HttpMethod.GET,httpEntity, String.class);
    }

    @Then("the response should include training {string}")
    public void responseIncludesTraining(String trainingName) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(trainingName);
    }

    @Then("the response should include trainers {string} and {string}")
    public void responseIncludesTrainers(String trainer1, String trainer2) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(trainer1);
        assertThat(response.getBody()).contains(trainer2);
    }

    @Then("the response should include trainer {string}")
    public void responseIncludesTrainer(String trainer) {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(trainer);
    }

    @When("I assign trainer {string} to trainee {string}")
    public void assignTrainer(String trainerName, String traineeName) {
        UpdateTraineeTrainerList trainer = new UpdateTraineeTrainerList(trainerName, 1L);
        HttpEntity<List<UpdateTraineeTrainerList>> entity = new HttpEntity<>(List.of(trainer),httpHeaders);
        response = testRestTemplate.exchange("/trainees/" + traineeName + "/trainers", HttpMethod.PATCH, entity, String.class);
    }

    @When("I delete trainee with username {string}")
    public void deleteTrainee(String username) {
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
       response = testRestTemplate.exchange("/trainees/" + username,HttpMethod.DELETE, httpEntity, String.class);
    }

    @When("I delete trainee with nonexistent username {string}")
    public void deleteNonExistentTrainee(String username) {
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        response = testRestTemplate.exchange("/trainees/" + username, HttpMethod.DELETE,httpEntity, String.class);
    }

    @Then("the trainee should be deleted successfully")
    public void verifyDelete() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Then("the error message should contain {string}")
    public void verifyErrorMessage(String expectedMsg) {
        assertThat(response.getBody()).contains(expectedMsg);
    }


}
