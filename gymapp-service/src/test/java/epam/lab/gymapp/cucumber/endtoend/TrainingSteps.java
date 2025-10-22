package epam.lab.gymapp.cucumber.endtoend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.cucumber.integration.dtos.TrainerWorkloadSummaryResponse;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.dto.response.login.LoginResponse;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<?> response;

    private final String baseGymAppUrl = "http://localhost:8082/gymapp";
    private final String gymAppUrl = "http://localhost:8082/gymapp/trainings";
    private final String workloadUrl = "http://localhost:8081/trainer-workload-service/trainer-workloads";

    private ObjectMapper objectMapper;
    private ResponseEntity<String> lastResponse;
    private HttpHeaders httpHeaders;

    private String jwtToken;
    private String trainerUsername;
    private String traineeUsername;
    private LocalDateTime trainingStart;
    private int trainerWorkloadBeforeUpdate;

    private final String activeMqContainerName = "activeMq";

    // --- GIVEN STEPS ---

    @Before
    public void setUpUrl() throws JsonProcessingException {

        objectMapper = new ObjectMapper();
        httpHeaders = new HttpHeaders();
        Credentials credentials = new Credentials("Emily.Brown", "pass789");

        ResponseEntity<String> response = restTemplate.postForEntity(baseGymAppUrl + "/users/login", credentials, String.class);
        String responseBody = response.getBody();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        jwtToken = loginResponse.getToken();
        httpHeaders.setBearerAuth(jwtToken);
    }

    @Given("ActiveMQ service is running")
    public void activemqServiceRunning() throws Exception {
        // Using Docker inspect
        Process process = Runtime.getRuntime().exec("docker inspect -f '{{.State.Running}}' " + activeMqContainerName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String status = reader.readLine();
        assertEquals("'true'", status, "ActiveMQ should be running");
    }

    @Given("ActiveMQ service is stopped")
    public void activemqServiceStopped() throws Exception {
        Process process = Runtime.getRuntime().exec("docker inspect -f '{{.State.Running}}' " + activeMqContainerName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String status = reader.readLine();
        assertEquals("'false'", status, "ActiveMQ should be stopped");
    }

    @Given("GymApp service is up")
    public void gymAppServiceIsUp() {
        ResponseEntity<String> health = restTemplate.getForEntity("http://localhost:8082/gymapp/actuator/health", String.class);
        assertEquals(HttpStatus.OK, health.getStatusCode());
        assertTrue(health.getBody().contains("\"status\":\"UP\""));
    }

    @Given("GymApp service is down")
    public void gymAppServiceIsDown() {

        try {
            restTemplate.getForEntity("http://localhost:8082/gymapp/actuator/health", String.class);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("\"status\":\"DOWN\""));

        }
    }



    @When("I add a new training with trainer {string} and trainee {string}")
    public void addNewTraining(String trainer, String trainee) {
        ResponseEntity<TrainerWorkloadSummaryResponse> summary = restTemplate.getForEntity(
                workloadUrl + "/" + trainer,
                TrainerWorkloadSummaryResponse.class);

        assertEquals(HttpStatus.OK, summary.getStatusCode());

        trainerWorkloadBeforeUpdate = summary.getBody().getYears().stream()
                .flatMap(y -> y.getMonths().stream()).mapToInt(m -> m.getTotalDuration()).sum();

        this.trainerUsername = trainer;
        this.traineeUsername = trainee;
        this.trainingStart = LocalDateTime.now().plusMinutes(10);

        TrainingAddDto newTraining = TrainingAddDto.builder()
                .trainingName("Some random")
                .trainerUserName(trainerUsername)
                .traineeUserName(traineeUsername)
                .trainingType("Cardio")
                .trainingDateStart(trainingStart)
                .duration(2)
                .build();

        HttpEntity<?> httpEntity = new HttpEntity<>(newTraining, httpHeaders);
        try {
            response = restTemplate.postForEntity(gymAppUrl, httpEntity, Object.class);
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }


    @Then("the training should be persisted in GymApp")
    public void trainingPersistedInGymApp() {
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<Object> trainingCheck = restTemplate.exchange(
                gymAppUrl + "?trainerUsername=" + trainerUsername +
                        "&traineeUsername=" + traineeUsername +
                        "&startTime=" + trainingStart,
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                Object.class);

        assertEquals(HttpStatus.OK, trainingCheck.getStatusCode(), "Training should be found in GymApp");
    }

    @Then("the trainer workload should be updated in TrainerWorkloadService")
    public void trainerWorkloadUpdated() {
        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<TrainerWorkloadSummaryResponse> summary = restTemplate.exchange(
                workloadUrl + "/" + trainerUsername,
                HttpMethod.GET,
                entity,
                TrainerWorkloadSummaryResponse.class);
       int trainerWorkloadAfterUpdate = summary.getBody().getYears().stream()
                .flatMap(y -> y.getMonths().stream()).mapToInt(m -> m.getTotalDuration()).sum();

        assertEquals(HttpStatus.OK, summary.getStatusCode());
        assertTrue(trainerWorkloadAfterUpdate>trainerWorkloadBeforeUpdate);

    }

    @Then("GymApp should return a service unavailable error")
    public void gymAppReturnsServiceUnavailable() {
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Then("the training should not be persisted in GymApp")
    public void trainingNotPersisted() {

        HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
        try {

            restTemplate.exchange(
                    gymAppUrl + "?trainerUsername=" + trainerUsername +
                            "&traineeUsername=" + traineeUsername +
                            "&startTime=" + trainingStart,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("404"));
        }

    }

    @Then("the trainer workload should not be updated in TrainerWorkloadService")
    public void trainerWorkloadNotUpdated() {
        ResponseEntity<TrainerWorkloadSummaryResponse> summary = restTemplate.getForEntity(
                workloadUrl + "/" + trainerUsername,
                TrainerWorkloadSummaryResponse.class);

        assertEquals(HttpStatus.OK, summary.getStatusCode());

        int trainerWorkloadAfterUpdate = summary.getBody().getYears().stream()
                .flatMap(y -> y.getMonths().stream()).mapToInt(m -> m.getTotalDuration()).sum();

        assertEquals(trainerWorkloadBeforeUpdate, trainerWorkloadAfterUpdate);
    }
}
