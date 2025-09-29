package epam.lab.gymapp.cucumber.component.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TraineeService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


public class TraineeControllerComponentSteps {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraineeService traineeService;

    private MvcResult lastMvcResult;


    @Given("trainee {string} exists")
    public void trainee_exists(String username) {
        Trainee trainee = Trainee.builder()
                .id(1L)
                .userName(username)
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDateTime.now().minusYears(25))
                .address("Earth")
                .trainers(List.of())
                .isActive(true)
                .build();
        when(traineeService.findByUsername(username)).thenReturn(trainee);
    }

    @Given("trainee {string} does not exist")
    public void trainee_does_not_exist(String username) {
        when(traineeService.findByUsername(username))
                .thenThrow(new EntityNotFoundException("Trainee not found"));
    }

    @Given("trainee {string} exists for delete")
    public void trainee_exists_for_delete(String username) {
        doNothing().when(traineeService).delete(username);
    }

    @Given("trainee {string} does not exist for delete")
    public void trainee_does_not_exist_for_delete(String username) {
        doThrow(new EntityNotFoundException("Trainee not found")).when(traineeService).delete(username);
    }

    @Given("trainee id {int} exists")
    public void trainee_id_exists(Integer id) {
        Trainee trainee = Trainee.builder()
                .id(id.longValue())
                .userName("john.doe")
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDateTime.now().minusYears(25))
                .address("Earth")
                .build();
        when(traineeService.findByUsername(anyString())).thenReturn(trainee);
    }

    @Given("trainee {string} exists with trainings")
    public void trainee_exists_with_trainings(String username) {
        Training t = Training.builder()
                .id(100L)
                .trainingName("Strength Training")
                .trainingType(TrainingType.builder().name("").build())
                .trainingDateStart(LocalDateTime.now())
                .trainer(Trainer.builder().userName("Santa").build())
                .trainee(Trainee.builder().userName(username).build())
                .build();
        when(traineeService.getTraineeTrainings(eq(username), any(), any(), any(), any()))
                .thenReturn(List.of(t));
    }

    @Given("trainee {string} exists with trainings and training id {int}")
    public void trainee_exists_with_trainings_and_training_id(String username, Integer trainingId) {
        Trainer trainer = Trainer.builder()
                .id(11L)
                .firstName("T")
                .lastName("One")
                .specialization(TrainingType.builder().name("Strength training").build())
                .isActive(true)
                .build();
        when(traineeService.updateTrainer(eq(username), anyList()))
                .thenReturn(List.of(trainer));
    }

    @Given("trainee {string} exists but not enrolled in training {int}")
    public void trainee_exists_but_not_enrolled_in_training(String username, Integer trainingId) {
        when(traineeService.updateTrainer(eq(username), anyList()))
                .thenThrow(new IllegalArgumentException("This trainee is not enrolled in the specified training"));
    }


    @When("I register a trainee with firstName {string}, lastName {string}, dateOfBirth {string} and address {string}")
    public void i_register_a_trainee(String firstName, String lastName, String dateOfBirth, String address) throws Exception {
        Trainee created = Trainee.builder()
                .id(1L)
                .userName(firstName.toLowerCase() + "." + lastName.toLowerCase())
                .password("encodedPass")
                .firstName(firstName)
                .lastName(lastName)
                .birthday(LocalDateTime.parse(dateOfBirth))
                .address(address)
                .build();
        when(traineeService.createProfile(any(Trainee.class))).thenReturn(created);

        Map<String, Object> payload = Map.of(
                "firstName", firstName,
                "lastName", lastName,
                "birthday", dateOfBirth,
                "address", address
        );

        lastMvcResult = mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andReturn();
    }

    @When("I register an invalid trainee body:")
    public void i_register_an_invalid_trainee_body(String docString) throws Exception {
        lastMvcResult = mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(docString))
                .andReturn();
    }

    @When("I request trainee by username {string}")
    public void i_request_trainee_by_username(String username) throws Exception {
        lastMvcResult = mockMvc.perform(get("/trainees/{username}", username)).andReturn();
    }

    @When("I delete trainee by username {string}")
    public void i_delete_trainee_by_username(String username) throws Exception {
        lastMvcResult = mockMvc.perform(delete("/trainees/{username}", username)).andReturn();
    }

    @When("I update trainee id {int} with payload:")
    public void i_update_trainee_with_payload(Integer id, String docString) throws Exception {
        Map<String, Object> payload = objectMapper.readValue(docString, new TypeReference<>() {});
        String userName = (String) payload.getOrDefault("userName", "unknown");

        Trainee updated = Trainee.builder()
                .id(id.longValue())
                .userName(userName)
                .firstName((String) payload.getOrDefault("firstName",""))
                .lastName((String) payload.getOrDefault("lastName",""))
                .birthday(LocalDateTime.parse((String)payload.getOrDefault("birthday", LocalDateTime.now().toString())))
                .address((String) payload.getOrDefault("address",""))
                .isActive((Boolean)payload.getOrDefault("isActive", true))
                .trainers(List.of())
                .build();

        if (id == 999) {
            when(traineeService.updateProfile(any())).thenThrow(new EntityNotFoundException("Entity with ID '999' not found"));
        } else {
            when(traineeService.updateProfile(any())).thenReturn(updated);
        }

        lastMvcResult = mockMvc.perform(put("/trainees/{id}", id)
                        .contentType("application/json")
                        .content(docString))
                .andReturn();
    }

    @When("I request trainings for trainee {string}")
    public void i_request_trainings_for_trainee(String username) throws Exception {
        lastMvcResult = mockMvc.perform(get("/trainees/{username}/trainings", username)).andReturn();
    }

    @When("I assign trainers to trainee {string} with payload:")
    public void i_assign_trainers_to_trainee_with_payload(String username, String docString) throws Exception {

        lastMvcResult = mockMvc.perform(patch("/trainees/{userName}/trainers", username)
                        .contentType("application/json")
                        .content(docString))
                .andReturn();
    }


    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        Assertions.assertNotNull(lastMvcResult, "No request was performed (lastMvcResult is null).");
        int actual = lastMvcResult.getResponse().getStatus();
        Assertions.assertEquals(expectedStatus.intValue(), actual,
                "Expected HTTP status " + expectedStatus + " but was " + actual + ". Response body: " + safeBody());
    }

    @Then("the response should contain username and password")
    public void the_response_should_contain_username_and_password() throws Exception {
        String body = safeBody();
        Map<String, Object> map = objectMapper.readValue(body, new TypeReference<>() {});
        Assertions.assertTrue(map.containsKey("username") || map.containsKey("userName"),
                "Response does not contain username. Body: " + body);
        Assertions.assertTrue(map.containsKey("password"),
                "Response does not contain password. Body: " + body);
    }

    @Then("the response should contain message {string}")
    public void the_response_should_contain_message(String expectedPart) {
        String body = safeBody();
        Assertions.assertTrue(body.toLowerCase().contains(expectedPart.toLowerCase()),
                "Expected response to contain '" + expectedPart + "' but was: " + body);
    }

    @Then("the response should contain trainee username {string}")
    public void the_response_should_contain_trainee_username(String username) {
        String body = safeBody();
        Assertions.assertTrue(body.contains(username),
                "Expected response body to contain trainee username '" + username + "' but was: " + body);
    }

    @Then("the response should contain trainings list")
    public void the_response_should_contain_trainings_list() {
        String body = safeBody();
        Assertions.assertTrue(body.toLowerCase().contains("strength") || body.contains("training"),
                "Expected response to contain trainings list; body: " + body);
    }

    @Then("the response should contain trainer {string}")
    public void the_response_should_contain_trainer(String trainerUsername) {
        String body = safeBody();
        Assertions.assertTrue(body.contains(trainerUsername),
                "Expected response to list trainer '" + trainerUsername + "' but was: " + body);
    }


    private String safeBody() {
        try {
            return lastMvcResult.getResponse().getContentAsString();
        } catch (Exception e) {
            return "<unable to read response body>";
        }
    }
}
