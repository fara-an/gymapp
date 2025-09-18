package epam.lab.gymapp.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TrainerControllerITSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestContextJwt testContextJwt;

    private HttpHeaders httpHeaders;

    private ResponseEntity<String> response;


    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Before
    public void login() throws Exception {
        Credentials creds = new Credentials("Emily.Brown", "pass789");
        ResponseEntity<String> resp = restTemplate.postForEntity(url("/users/login"), creds, String.class);

        LoginResponse loginResponse = objectMapper.readValue(resp.getBody(), LoginResponse.class);
        testContextJwt.setJwtToken(loginResponse.getToken());
        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(testContextJwt.getJwtToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    }

    @When("I register a trainer with firstName {string}, lastName {string} and specialization {string}")
    public void registerTrainer(String firstName, String lastName, String specialization) {

        TrainerRegistrationBody registrationBody = TrainerRegistrationBody
                .builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingType(specialization)
                .build();
        HttpEntity<?> httpEntity = new HttpEntity<>(registrationBody, httpHeaders);

        response = restTemplate.postForEntity("/trainers", httpEntity, String.class);
    }

    @When("I get trainer {string}")
    public void getTrainer(String username) {
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        response = restTemplate.exchange("/trainers/" + username, HttpMethod.GET, httpEntity, String.class);
    }

    @When("I update trainer with id {int} to have firstName {string}, lastName {string}, username {string}, active {string}")
    public void updateTrainer(int id, String firstName, String lastName, String username, String active) {
        UpdateTrainerDto dto = UpdateTrainerDto.builder()
                .userName(username)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(Boolean.parseBoolean(active))
                .build();
        HttpEntity<?> entity = new HttpEntity<>(dto, httpHeaders);
        response = restTemplate.exchange("/trainers/" + id, HttpMethod.PUT, entity, String.class);
    }

    @When("I get unassigned trainers for trainee {string}")
    public void getUnassignedTrainers(String username) {
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        response = restTemplate.exchange("/trainers/" + username + "/unassigned-trainers", HttpMethod.GET, entity, String.class);
    }

    @When("I get trainings for trainer {string}")
    public void getTrainerTrainings(String username) {
        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        response = restTemplate.exchange("/trainers/" + username + "/trainings", HttpMethod.GET, entity, String.class);
    }

    @Then("Trainer Controller: the response status should be {int}")
    public void responseStatusShouldBe(int status) {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
    }

    @Then("Trainer Controller: the response should contain username and password")
    public void responseContainsUsernameAndPassword() {
        assertThat(response.getBody()).contains("username").contains("password");
    }

    @Then("Trainer Controller: the response should contain trainer firstName {string}")
    public void responseContainsTrainerFirstName(String firstName) {
        assertThat(response.getBody()).contains(firstName);
    }

    @Then("Trainer Controller: the response should contain trainer {string}")
    public void responseContainsTrainer(String trainerName) {
        assertThat(response.getBody()).contains(trainerName);
    }

    @Then("Trainer Controller: the response should contain training {string}")
    public void responseContainsTraining(String trainingName) {
        assertThat(response.getBody()).contains(trainingName);
    }
}
