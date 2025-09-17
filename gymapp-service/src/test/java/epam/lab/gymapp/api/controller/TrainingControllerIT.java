package epam.lab.gymapp.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import epam.lab.gymapp.GymApplication;
import epam.lab.gymapp.configuration.NoSecurityConfig;
import epam.lab.gymapp.configuration.NoServiceConfig;
import epam.lab.gymapp.configuration.TestHibernateConfig;
import epam.lab.gymapp.configuration.TrainerWorkloadClientServiceStubConfig;
import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@Testcontainers
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {GymApplication.class, NoSecurityConfig.class})
@Import({TrainerWorkloadClientServiceStubConfig.class, TestHibernateConfig.class, NoServiceConfig.class})
@ActiveProfiles("test")
class TrainingControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0").withReuse(false);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private TrainingAddDto trainingAddDto;

    @BeforeEach
    void setUp() {
        trainingAddDto = TrainingAddDto.builder()
                .traineeUserName("Emily.Brown")
                .trainerUserName("John.Doe")
                .trainingName("working on biceps")
                .trainingType("Strength Training")
                .trainingDateStart(LocalDateTime.now().plusDays(1))
                .duration(60)
                .build();
    }

    @Test
    void debugContainerState() {
        System.out.println("Container ID: " + postgres.getContainerId());
        System.out.println("Container is running: " + postgres.isRunning());
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
        var trainings = jdbcTemplate.queryForList("SELECT * FROM training");
        System.out.println(trainings);
    }

    @Test
    void addTraining_ShouldPersistAndReturnOk() throws JsonProcessingException {
        ResponseEntity<TrainingResponse> response = restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                TrainingResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTrainingName()).isEqualTo("working on biceps");

    }

    @Test
    void addTraining_ShouldNotPersistWhenEntityNotFound(){
        trainingAddDto.setTraineeUserName("Somebody that i used to know");
        ResponseEntity<MessageResponse> response =restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Requested entity  was not found");
    }

    @Test
    void addTraining_ShouldNotPersistWhenTrainerNotFound() {
        trainingAddDto.setTrainerUserName("Ghost.Trainer");

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                MessageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Requested entity  was not found");
    }

    @Test
    void addTraining_ShouldFailOnInvalidTrainingType() {
        trainingAddDto.setTrainingType("Yoga"); // assume John.Doe specialization ≠ Yoga

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                MessageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("TrainingServiceImpl: Trainer specialization 'Strength Training' does not match required 'Yoga'");
    }

    @Test
    void addTraining_ShouldFailOnScheduleConflict() {
        ResponseEntity<TrainingResponse> first = restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                TrainingResponse.class
        );
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK);

        TrainingAddDto conflicting = TrainingAddDto.builder()
                .traineeUserName("Emily.Brown")
                .trainerUserName("John.Doe")
                .trainingName("Chest day clash")
                .trainingType("Strength Training")
                .trainingDateStart(trainingAddDto.getTrainingDateStart()) // same start
                .duration(30)
                .build();

        ResponseEntity<MessageResponse> conflictResponse = restTemplate.postForEntity(
                "/trainings",
                conflicting,
                MessageResponse.class
        );

        assertThat(conflictResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(conflictResponse.getBody()).isNotNull();
        assertThat(conflictResponse.getBody().getMessage()).contains("session that overlaps with this time window");
    }

    @Test
    void addTraining_ShouldFailWhenTrainerWorkloadServiceFails() {
           trainingAddDto.setTrainingName("force-fail");

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
                "/trainings",
                trainingAddDto,
                MessageResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
    }



}