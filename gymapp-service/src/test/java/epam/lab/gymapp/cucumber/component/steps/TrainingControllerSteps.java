package epam.lab.gymapp.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.cucumber.TestContextJwt;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingService;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TrainingControllerSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TestContextJwt testContextJwt;

    private ResponseEntity<String> response;

    private static TrainingAddDto trainingAddDto;

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

    @BeforeAll()
    public static void before_all() {
        trainingAddDto = TrainingAddDto.builder()
                .traineeUserName("Emily.Brown")
                .trainerUserName("John.Doe")
                .trainingName("working on biceps")
                .trainingType("Strength Training")
                .trainingDateStart(LocalDateTime.now().plusDays(1))
                .duration(60)
                .build();
    }

    @Given("a trainer {string} exists with specialization {string}")
    public void a_trainer_exists_with_specialization(String trainerUsername, String specialization) {
        Trainer foundTrainer = trainerService.findByUsername(trainerUsername);
        Assertions.assertEquals(specialization, foundTrainer.getSpecialization().getName());

    }

    @Given("a trainer {string} exists but with different specialization other than {string}")
    public void a_trainer_exists_with_different_specialization(String trainerUsername, String specialization) {
        Trainer foundTrainer = trainerService.findByUsername(trainerUsername);
        Assertions.assertNotEquals(specialization, foundTrainer.getSpecialization().getName());

    }

    @Given("a trainee {string} exists")
    public void a_trainee_exists(String traineeUsername) {
        Trainee foundTrainee = traineeService.findByUsername(traineeUsername);
        Assertions.assertNotNull(foundTrainee);
    }

    @Given("a training exists between trainer {string} and trainee {string} at {string}")
    public void a_training_exists(String trainer, String trainee, String start) {
        Training training = trainingService.findTraining(trainer, trainee, LocalDateTime.parse(start));
        Assertions.assertNotNull(training);
    }


    @When("I send a POST request to {string} with trainingDto:")
    public void i_send_post_request(String url) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(trainingAddDto), headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @When("I send a POST request to {string} with non-existent trainingDto:")
    public void i_send_post_request_with_non_existent_dto(String url) throws Exception {
        trainingAddDto.setTrainingType("pilates");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(trainingAddDto), headers);
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @When("I send a DELETE request to {string}")
    public void i_send_delete_request(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }

    @When("I send a GET request to {string}")
    public void i_send_get_request(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(testContextJwt.getJwtToken());
        HttpEntity<String> request = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    @Then("TrainingController the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
    }

    @Then("TrainingController the response should contain {string}")
    public void the_response_should_contain(String text) {
        assertThat(response.getBody()).contains(text);
    }


}
