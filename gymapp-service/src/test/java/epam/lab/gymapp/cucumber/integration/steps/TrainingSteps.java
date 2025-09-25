package epam.lab.gymapp.cucumber.integration.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.cucumber.TestContextJwt;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static epam.lab.gymapp.cucumber.integration.CucumberSpringITConfiguration.activeMq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author DarkTech
 */
public class TrainingSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.trainerWorkload}")
    private String queueName;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<?> lastResponse;
    private Object lastQueueMessage;
    private TrainingAddDto lastCreatedTraining;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TestContextJwt testContextJwt;

    HttpHeaders httpHeaders;

    @Before
    public void login() throws JsonProcessingException {
        Credentials credentials = new Credentials("Emily.Brown", "pass789");
        ResponseEntity<String> responseBody = restTemplate.postForEntity("http://localhost:" + port + "/users/login", credentials, String.class);
        LoginResponse loginResponse = objectMapper.readValue(responseBody.getBody(), LoginResponse.class);
        testContextJwt.setJwtToken(loginResponse.getToken());
        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(testContextJwt.getJwtToken());
    }


    @Given("the system has a trainer {string} with specialization {string}")
    public void givenTrainer(String trainer, String specialization) {
        Trainer foundTrainer = trainerService.findByUsername(trainer);
        assertNotNull(foundTrainer);
        assertEquals(specialization, foundTrainer.getSpecialization().getName());
    }

    @Given("the system has a trainee {string}")
    public void givenTrainee(String trainee) {
        Trainee foundTrainee = traineeService.findByUsername(trainee);
        assertNotNull(foundTrainee);
    }

    @When("I add a training with name {string} type {string} duration {int}")
    public void iAddATraining(String trainingName, String trainingType, Integer duration) {
        TrainingAddDto dto = new TrainingAddDto();
        dto.setTrainingName(trainingName);
        dto.setTrainerUserName("John.Doe");
        dto.setTraineeUserName("Emily.Brown");
        dto.setTrainingType(trainingType);
        dto.setTrainingDateStart(LocalDateTime.now().plusDays(3).withNano(0));
        dto.setDuration(duration);
        HttpEntity<?> httpEntity = new HttpEntity<>(dto, httpHeaders);

        lastCreatedTraining = dto;
        lastResponse = restTemplate.postForEntity("/trainings", httpEntity, Object.class);

        try {
            lastQueueMessage = jmsTemplate.receiveAndConvert(queueName);
        } catch (Exception e) {
            lastQueueMessage = null;
        }
    }

    @Given("I have an existing training with name {string} type {string} duration {int}")
    public void iHaveAnExistingTraining(String trainingName, String trainingType, Integer duration) {
        iAddATraining(trainingName, trainingType, duration);
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @When("I delete this training")
    public void iDeleteThisTraining() {
        String url = String.format(
                "/trainings?trainerUsername=%s&traineeUsername=%s&startTime=%s",
                lastCreatedTraining.getTrainerUserName(),
                lastCreatedTraining.getTraineeUserName(),
                lastCreatedTraining.getTrainingDateStart().withNano(0)
        );
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        lastResponse = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Object.class);

        try {
            lastQueueMessage = jmsTemplate.receiveAndConvert(queueName);
        } catch (Exception e) {
            lastQueueMessage = null;
        }
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @Then("a message should be sent to the queue")
    public void aMessageShouldBeSentToTheQueue() {
        assertThat(lastQueueMessage).isNotNull();
    }

    @Then("no message should be delivered to the queue")
    public void noMessageShouldBeDeliveredToTheQueue() {
        assertThat(lastQueueMessage).isNull();
    }

    @Given("the message broker is stopped")
    public void theMessageBrokerIsStopped() {
      activeMq.stop();
    }


}
