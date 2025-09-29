package epam.lab.gymapp.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainerControllerComponentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    TrainingTypeService trainingTypeService;

    private ResultActions resultActions;

    @When("I register a trainer with firstName {string}, lastName {string}, trainingType {string}")
    public void i_register_a_trainer(String firstName, String lastName, String trainingType) throws Exception {
        TrainerRegistrationBody body = TrainerRegistrationBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingType(trainingType)
                .build();

        TrainingType specialization = TrainingType.builder().name(trainingType).build();
        Mockito.when(trainingTypeService.findByName(trainingType)).thenReturn(specialization);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userName(firstName.toLowerCase() + "_" + lastName.toLowerCase())
                .password("encodedPass")
                .specialization(specialization)
                .build();

        Mockito.when(trainerService.createProfile(Mockito.any())).thenReturn(trainer);

        resultActions = mockMvc.perform(post("/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @When("I register a trainer with firstName {string}, lastName {string}, non-existent trainingType {string}")
    public void i_register_a_trainerWithNonExistentTrainingType(String firstName, String lastName, String trainingType) throws Exception {
        TrainerRegistrationBody body = TrainerRegistrationBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .trainingType(trainingType)
                .build();

        TrainingType specialization = TrainingType.builder().name(trainingType).build();
        Mockito.when(trainingTypeService.findByName(trainingType)).thenThrow(EntityNotFoundException.class);

        Trainer trainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userName(firstName.toLowerCase() + "_" + lastName.toLowerCase())
                .password("encodedPass")
                .specialization(specialization)
                .build();

        Mockito.when(trainerService.createProfile(Mockito.any())).thenReturn(trainer);

        resultActions = mockMvc.perform(post("/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @Then("Trainer Controller: the response status should be {int}")
    public void the_response_status_should_be(int status) throws Exception {
        resultActions.andExpect(status().is(status));
    }

    @Then("Trainer Controller: the response should contain username {string} and password {string}")
    public void the_response_should_contain_username_and_password(String username, String password) throws Exception {
        resultActions.andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").value(password));
    }

    @Then("Trainer Controller: the response error should contain {string}")
    public void the_response_error_should_contain(String errorMessage) throws Exception {
        resultActions.andExpect(content().string(containsString(errorMessage)));
    }

    @When("I request trainer with username {string}")
    public void i_request_trainer_with_username(String username) throws Exception {
        Trainer trainer = Trainer.builder()
                .userName(username)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .specialization(TrainingType.builder().build())
                .trainees(List.of())
                .build();

        Mockito.when(trainerService.findByUsername(username)).thenReturn(trainer);

        resultActions = mockMvc.perform(get("/trainers/{username}", username));
    }

    @When("I request non-existent trainer with username {string}")
    public void iRequestTrainerWithUsername(String username) throws Exception {


        Mockito.when(trainerService.findByUsername(username)).thenThrow(EntityNotFoundException.class);

        resultActions = mockMvc.perform(get("/trainers/{username}", username));
    }

    @Then("Trainer Controller: the response should contain trainer firstName {string} and lastName {string}")
    public void the_response_should_contain_trainer(String firstName, String lastName) throws Exception {
        resultActions.andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));
    }

    @When("I update trainer with id {long} with firstName {string}, lastName {string}, username {string}")
    public void i_update_trainer(Long id, String firstName, String lastName, String username) throws Exception {
        UpdateTrainerDto body = UpdateTrainerDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userName(username)
                .isActive(true)
                .build();

        Trainer updated = Trainer.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .userName(username)
                .trainees(List.of())
                .specialization(TrainingType.builder().build())
                .isActive(true)
                .build();

        Mockito.when(trainerService.updateProfile(Mockito.any())).thenReturn(updated);

        resultActions = mockMvc.perform(put("/trainers/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }
}
