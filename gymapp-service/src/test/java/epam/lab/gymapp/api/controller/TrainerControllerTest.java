package epam.lab.gymapp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.configuration.NoSecurityConfig;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = TrainerController.class)
@Import(NoSecurityConfig.class)
public class TrainerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TrainerService trainerService;

    @MockitoBean
    TrainingTypeService trainingTypeService;

    @MockitoBean
    MeterRegistry meterRegistry;

    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final String FIRSTNAME = "John";
    private static final String LASTNAME = "Doe";
    private static final String USERNAME = FIRSTNAME + "." + LASTNAME;
    private static final String PASSWORD = "pass123";

    @Test
    public void register_ShouldReturn200AndCreatedTrainerRegistrationResponse() throws Exception {


        TrainingType trainingType = TrainingType.builder().name(TRAINING_TYPE_NAME).build();
        TrainerRegistrationBody trainerRegistrationBody = TrainerRegistrationBody.builder()
                .firstName(FIRSTNAME)
                .lastName(LASTNAME)
                .trainingType(trainingType.getName())
                .build();

        Trainer trainer = Trainer.builder()
                .userName(USERNAME)
                .password(PASSWORD)
                .build();

        when(trainingTypeService.findByName(TRAINING_TYPE_NAME)).thenReturn(trainingType);
        when(trainerService.createProfile(any(Trainer.class))).thenReturn(trainer);

        mockMvc.perform(post("/trainers")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRegistrationBody)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.password").value(PASSWORD));

    }

    @Test
    public void register_ShouldNotCreateTrainer_WhenTrainingTypeNotFound() throws Exception {
        TrainerRegistrationBody registrationDto = new TrainerRegistrationBody();
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");
        registrationDto.setTrainingType("Unknown");

        when(trainingTypeService.findByName("Unknown"))
                .thenThrow(new EntityNotFoundException("Training type not found"));

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isNotFound());

        verify(trainerService, never()).createProfile(any());
    }

    @Test
    public void getTrainer_ShouldReturnTrainerGetResponse() throws Exception {
        TrainingType trainingType = TrainingType.builder().name(TRAINING_TYPE_NAME).build();

        Trainer trainer = Trainer
                .builder()
                .userName(USERNAME)
                .firstName(FIRSTNAME)
                .lastName(LASTNAME)
                .specialization(trainingType)
                .isActive(true)
                .trainees(List.of())
                .build();

        TrainerGetResponse trainerGetResponse = TrainerGetResponse.builder()
                .firstName(FIRSTNAME)
                .lastName(LASTNAME)
                .specialization(trainingType)
                .active(true)
                .trainees(List.of())
                .build();

        when(trainerService.findByUsername(USERNAME)).thenReturn(trainer);

        try (MockedStatic<TrainerMapper> mockedStatic = Mockito.mockStatic(TrainerMapper.class)) {
            mockedStatic.when(() -> TrainerMapper.dtoWithTraineeList(any(Trainer.class))).thenReturn(trainerGetResponse);

        }

        mockMvc.perform(get("/trainers/{username}", USERNAME))
                .andExpect(jsonPath("$.firstName").value(FIRSTNAME))
                .andExpect(jsonPath("$.lastName").value(LASTNAME))
                .andExpect(jsonPath("$.specialization.name").value(trainerGetResponse.getSpecialization().getName()))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees").isEmpty());
    }

    @Test
    public void getTrainer_ShouldNotReturnTrainerGetResponse_WhenExceptionThrown() throws Exception {

        when(trainerService.findByUsername(USERNAME)).thenThrow(new EntityNotFoundException("Trainer not found"));
        mockMvc.perform(get("/trainers/{username}", USERNAME))
                .andExpect(status().isNotFound());

        try (MockedStatic<TrainerMapper> mockedStatic = Mockito.mockStatic(TrainerMapper.class)) {
            mockedStatic.verifyNoInteractions();
        }
    }

    @Test
    public void updateTrainer_ShouldReturnUpdatedTrainer() throws Exception {
        Long id = 1L;
        UpdateTrainerDto updateDto = new UpdateTrainerDto();
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setUserName("john.doe");
        updateDto.setActive(true);

        Trainer updatedTrainer = Trainer.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .isActive(true)
                .trainees(Collections.emptyList())
                .build();

        TrainerGetResponse response = TrainerGetResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .trainees(Collections.emptyList())
                .build();

        when(trainerService.updateProfile(any(Trainer.class)))
                .thenReturn(updatedTrainer);

        try (MockedStatic<TrainerMapper> mockedStatic = Mockito.mockStatic(TrainerMapper.class)) {
            mockedStatic.when(() -> TrainerMapper.dtoWithTraineeList(updatedTrainer))
                    .thenReturn(response);

            mockMvc.perform(put("/trainers/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        {
                                          "firstName": "John",
                                          "lastName": "Doe",
                                          "userName": "john.doe",
                                          "isActive": true
                                        }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.active").value(true));

            mockedStatic.verify(() -> TrainerMapper.dtoWithTraineeList(updatedTrainer), times(1));
        }
    }

    @Test
    public void getTrainersNotAssignedToTrainee_ShouldReturnList() throws Exception {
        String username = "trainee1";
        TrainingType specialization = TrainingType.builder().name("Cardio").build();

        Trainer trainer1 = Trainer.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .userName("alice.smith")
                .specialization(specialization)
                .isActive(true)
                .build();

        Trainer trainer2 = Trainer.builder()
                .id(2L)
                .firstName("Bob")
                .lastName("Jones")
                .userName("bob.jones")
                .specialization(specialization)
                .isActive(false)
                .build();

        List<Trainer> trainerList = List.of(trainer1, trainer2);

        TrainerWithoutTraineesResponse response1 = TrainerWithoutTraineesResponse.builder()
                .firstName("Alice")
                .lastName("Smith")
                .specialization(specialization)
                .isActive(true)
                .build();

        TrainerWithoutTraineesResponse response2 = TrainerWithoutTraineesResponse.builder()
                .firstName("Bob")
                .lastName("Jones")
                .specialization(specialization)
                .isActive(false)
                .build();

        when(trainerService.trainersNotAssignedToTrainee(username)).thenReturn(trainerList);

        try (MockedStatic<TrainerMapper> mockedStatic = Mockito.mockStatic(TrainerMapper.class)) {
            mockedStatic.when(() -> TrainerMapper.dtoWithoutTraineeList(trainer1)).thenReturn(response1);
            mockedStatic.when(() -> TrainerMapper.dtoWithoutTraineeList(trainer2)).thenReturn(response2);

            mockMvc.perform(get("/trainers/{username}/unassigned-trainers", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].firstName").value("Alice"))
                    .andExpect(jsonPath("$[0].lastName").value("Smith"))
                    .andExpect(jsonPath("$[0].active").value(true))
                    .andExpect(jsonPath("$[1].firstName").value("Bob"))
                    .andExpect(jsonPath("$[1].lastName").value("Jones"))
                    .andExpect(jsonPath("$[1].active").value(false));

            mockedStatic.verify(() -> TrainerMapper.dtoWithoutTraineeList(trainer1), times(1));
            mockedStatic.verify(() -> TrainerMapper.dtoWithoutTraineeList(trainer2), times(1));
        }
    }

    @Test
    public void getTrainerTrainings_ShouldReturnTrainingList() throws Exception {

        String username = "trainer1";
           String traineeName = "John";
        String trainingType = "Cardio";

        Training training1 = Training.builder()
                .id(1L)
                .trainee(null)
                .trainer(null)
                .trainingType(new TrainingType("Cardio"))
                .trainingDateStart(LocalDateTime.of(2024, 5, 1, 10, 0))
                .build();

        Training training2 = Training.builder()
                .id(2L)
                .trainee(null)
                .trainer(null)
                .trainingType(new TrainingType("Cardio"))
                .trainingDateStart(LocalDateTime.of(2024, 6, 1, 15, 0))
                .build();

        List<Training> trainings = List.of(training1, training2);

        TrainingResponse response1 = TrainingResponse.builder()
                .traineeName("John Doe")
                .trainerName("Alice Smith")
                .trainingType("Cardio")
                .trainingDateStart(LocalDateTime.of(2024, 6, 1, 15, 0))
                .build();

        TrainingResponse response2 = TrainingResponse.builder()
                .traineeName("Jane Roe")
                .trainerName("Alice Smith")
                .trainingType("Cardio")
                .trainingDateStart(LocalDateTime.of(2024, 6, 1, 15, 0))
                .build();

        when(trainerService.getTrainerTrainings(eq(username), any(), any(), eq(traineeName), eq(trainingType)))
                .thenReturn(trainings);

        try (MockedStatic<TrainingMapper> mockedStatic = Mockito.mockStatic(TrainingMapper.class)) {
            mockedStatic.when(() -> TrainingMapper.trainingWithTrainer(training1)).thenReturn(response1);
            mockedStatic.when(() -> TrainingMapper.trainingWithTrainer(training2)).thenReturn(response2);

            mockMvc.perform(get("/trainers/{username}/trainings", username)
                            .param("from", "2024-01-01T00:00:00")
                            .param("to", "2024-12-31T23:59:00")
                            .param("traineeName", traineeName)
                            .param("trainingType", trainingType))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].traineeName").value("John Doe"))
                    .andExpect(jsonPath("$[1].traineeName").value("Jane Roe"));

            mockedStatic.verify(() -> TrainingMapper.trainingWithTrainer(training1), times(1));
            mockedStatic.verify(() -> TrainingMapper.trainingWithTrainer(training2), times(1));
        }
    }


}



