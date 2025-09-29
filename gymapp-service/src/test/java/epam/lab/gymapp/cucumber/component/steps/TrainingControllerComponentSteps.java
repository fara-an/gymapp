package epam.lab.gymapp.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.UserInputException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingControllerComponentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TrainingTypeService trainingTypeService;


    private LocalDateTime trainingStart;

    private MvcResult lastMvcResult;

    @When("I add a training with trainee {string}, trainer {string}, name {string}, type {string}, start {string} and duration {int}")
    public void iAddTraining(String trainee, String trainer, String name, String type, String start, Integer duration) throws Exception {
        trainingStart = LocalDateTime.parse(start);
        TrainingAddDto dto = TrainingAddDto.builder()
                .traineeUserName(trainee)
                .trainerUserName(trainer)
                .trainingName(name)
                .trainingType(type) //yoga
                .trainingDateStart(trainingStart)
                .duration(duration)
                .build();


        when(trainingService.addTraining(dto)).thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

    }

    @When("I add a training with trainee {string}, trainer {string}, name {string}, type {string}, start {string} and duration {int} that conflicts trainee")
    public void iAddTrainingWithConflict(String trainee, String trainer, String name, String type, String start, Integer duration) throws Exception {
        trainingStart = LocalDateTime.parse(start);
        TrainingAddDto dto = TrainingAddDto.builder()
                .traineeUserName(trainee)
                .trainerUserName(trainer)
                .trainingName(name)
                .trainingType(type)
                .trainingDateStart(trainingStart)
                .duration(duration)
                .build();

        when(trainingService.addTraining(any())).thenThrow(new UserInputException("Trainee already has a session that overlaps with this time window"));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Problem with user input"));
    }

    @When("I delete training with trainer {string}, trainee {string}, start {string}")
    public void iDeleteTraining(String trainer, String trainee, String start) throws Exception {
        trainingStart = LocalDateTime.parse(start);

        when(trainingService.deleteTraining(eq(trainer), eq(trainee), eq(trainingStart)))
                .thenReturn(org.springframework.http.ResponseEntity.ok().build());

        mockMvc.perform(delete("/trainings")
                        .param("trainerUsername", trainer)
                        .param("traineeUsername", trainee)
                        .param("startTime", trainingStart.toString()))
                .andExpect(status().isOk());
    }

    @When("I delete non-existent training with trainer {string}, trainee {string}, start {string}")
    public void iDeleteNonExistentTraining(String trainer, String trainee, String start) throws Exception {
        trainingStart = LocalDateTime.parse(start);

        when(trainingService.deleteTraining(eq(trainer), eq(trainee), eq(trainingStart)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(delete("/trainings")
                        .param("trainerUsername", trainer)
                        .param("traineeUsername", trainee)
                        .param("startTime", trainingStart.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Error occurred during database interaction"));
    }

    @When("I request training with trainer {string}, trainee {string}, start {string}")
    public void iRequestTraining(String trainer, String trainee, String start) throws Exception {
        trainingStart = LocalDateTime.parse(start);

        Training mockTraining = Training.builder()
                .id(1L)
                .trainer(Trainer.builder().userName(trainer).build())
                .trainee(Trainee.builder().userName(trainee).build())
                .trainingName("Morning Session")
                .trainingType(new TrainingType("Yoga"))
                .trainingDateStart(trainingStart)
                .trainingDateEnd(trainingStart.plusMinutes(60))
                .duration(60)
                .build();

        when(trainingService.findTraining(eq(trainer), eq(trainee), eq(trainingStart)))
                .thenReturn(mockTraining);

        mockMvc.perform(get("/trainings")
                        .param("trainerUsername", trainer)
                        .param("traineeUsername", trainee)
                        .param("startTime", trainingStart.toString()))
                .andExpect(status().isOk());

    }

    @When("I request a training that does not exist with trainer {string}, trainee {string}, start {string}")
    public void iRequestNonExistentTraining(String trainer, String trainee, String start) throws Exception {
        trainingStart = LocalDateTime.parse(start);

        when(trainingService.findTraining(eq(trainer), eq(trainee), eq(trainingStart)))
                .thenThrow(new EntityNotFoundException("Training not found"));

        mockMvc.perform(get("/trainings")
                        .param("trainerUsername", trainer)
                        .param("traineeUsername", trainee)
                        .param("startTime", trainingStart.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Error occurred during database interaction"));
    }

    @Then("the response should contain the training with name {string}, trainer {string}, trainee {string} and type {string}")
    public void thenResponseContainsTraining(String name, String trainer, String trainee, String type) {
    }

    @Then("the response should fail with status {int} and message {string}")
    public void thenResponseShouldFail(Integer status, String message) {
    }

    @Then("the training should be deleted successfully")
    public void thenTrainingDeletedSuccessfully() {
    }

    @Then("the response should contain 200 status code")
    public void thenShouldReturn200Message() {
    }
}
