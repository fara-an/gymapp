package epam.lab.gymapp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.lab.gymapp.config.metric.MetricsConfig;
import epam.lab.gymapp.configuration.NoSecurityConfig;
import epam.lab.gymapp.configuration.NoServiceConfig;
import epam.lab.gymapp.dto.mapper.TraineeMapper;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeDto;
import epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList;
import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.filter.perrequest.JwtAuthenticationFilter;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TraineeService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(value = TraineeController.class, excludeFilters =@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE ,classes = JwtAuthenticationFilter.class))
@Import({NoSecurityConfig.class, NoServiceConfig.class})
@ImportAutoConfiguration(exclude = MetricsConfig.class)
public class TraineeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TraineeService traineeService;


    @MockitoBean
    MeterRegistry meterRegistry;


    @Test
    public void register_ShouldReturn200AndCreatedTraineeResponse() throws Exception {
        TraineeRegistrationBody registrationBody = new TraineeRegistrationBody();
        registrationBody.setFirstName("Alice");
        registrationBody.setLastName("Smith");
        registrationBody.setDateOfBirth(LocalDateTime.of(2000, 1, 1, 0, 0));
        registrationBody.setAddress("123 Gym St");

        Trainee trainee = Trainee.builder()
                .firstName("Alice")
                .lastName("Smith")
                .birthday(LocalDateTime.of(2000, 1, 1, 0, 0))
                .address("123 Gym St")
                .userName("alice123")
                .password("securepass")
                .build();
        try (MockedStatic<TraineeMapper> mockedMapper = Mockito.mockStatic(TraineeMapper.class)) {
            mockedMapper.when(() -> TraineeMapper.fromDtoToTrainee(Mockito.any(TraineeRegistrationBody.class)))
                    .thenReturn(trainee);

            mockMvc.perform(post("/trainees")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registrationBody)))

                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("alice123"))
                    .andExpect(jsonPath("$.password").value("securepass"));

            verify(traineeService).createProfile(trainee);


        }


    }

    @Test
    void getTrainee_ShouldReturnTraineeWithTrainers() throws Exception {
        String username = "john123";

        Trainee trainee = Trainee.builder()
                .userName("john123")
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDateTime.of(1995, 5, 10, 0, 0))
                .address("123 Fit Lane")
                .isActive(true)
                .trainers(List.of())
                .build();

        TraineeGetResponse response = TraineeGetResponse.builder()
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDateTime.of(1995, 5, 10, 0, 0))
                .address("123 Fit Lane")
                .isActive(true)
                .trainers(List.of())
                .build();

        Mockito.when(traineeService.findByUsername(username)).thenReturn(trainee);

        try (MockedStatic<TraineeMapper> mockedStatic = Mockito.mockStatic(TraineeMapper.class)) {
            mockedStatic.when(() -> TraineeMapper.traineeWithTrainers(Mockito.any(Trainee.class)))
                    .thenReturn(response);

            mockMvc.perform(get("/trainees/{username}", username)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.birthday").value("1995-05-10T00:00:00"))
                    .andExpect(jsonPath("$.address").value("123 Fit Lane"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.trainers").isArray())
                    .andExpect(jsonPath("$.trainers").isEmpty());
        }
    }

    @Test
    void updateTrainee_ShouldReturnTraineeWithTrainers() throws Exception {
        Long id = 1L;
        UpdateTraineeDto updateTraineeDto = UpdateTraineeDto.builder()
                .userName("john123")
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDateTime.of(1995, 5, 10, 0, 0))
                .address("123 Fit Lane")
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .id(id)
                .userName(updateTraineeDto.getUserName())
                .firstName(updateTraineeDto.getFirstName())
                .lastName(updateTraineeDto.getLastName())
                .birthday(updateTraineeDto.getBirthday())
                .address(updateTraineeDto.getAddress())
                .isActive(updateTraineeDto.getIsActive())
                .build();


        TraineeGetResponse response = TraineeGetResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .birthday(trainee.getBirthday())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .trainers(List.of())
                .build();

        when(traineeService.updateProfile(Mockito.any(Trainee.class))).thenReturn(trainee);

        try (MockedStatic<TraineeMapper> mockedStatic = Mockito.mockStatic(TraineeMapper.class)) {
            mockedStatic.when(() -> TraineeMapper.traineeWithTrainers(Mockito.any(Trainee.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/trainees/1")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateTraineeDto)))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.birthday").value("1995-05-10T00:00:00"))
                    .andExpect(jsonPath("$.address").value("123 Fit Lane"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.trainers").isArray())
                    .andExpect(jsonPath("$.trainers").isEmpty());

            verify(traineeService).updateProfile(Mockito.any(Trainee.class));

        }
    }


    @Test
    void getTraineeTrainings_ShouldReturnListOfTrainingResponses() throws Exception {
        String traineeName = "john123";
        String trainerName = "alice";
        String trainingType = "cardio";
        String trainingName = "cardio time";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime from = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2023, 12, 31, 0, 0);

        Training mockTraining = Training.builder()
                .trainingType(TrainingType.builder().name(trainingType).build())
                .trainingName(trainingName)
                .trainingDateStart(from)
                .trainer(Trainer.builder().userName(trainerName).build())
                .trainee(Trainee.builder().userName(traineeName).build())
                .build();

        List<Training> mockTrainings = List.of(mockTraining);

        when(traineeService.getTraineeTrainings(eq(traineeName), eq(from), eq(to), eq(trainerName), eq(trainingType)))
                .thenReturn(mockTrainings);

        TrainingResponse mapped = TrainingMapper.trainingWithTrainee(mockTraining);

        mockMvc.perform(get("/trainees/{userName}/trainings", traineeName)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("trainerName", trainerName)
                        .param("trainingType", trainingType)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainerName").value(trainerName))
                .andExpect(jsonPath("$[0].traineeName").value(traineeName))
                .andExpect(jsonPath("$[0].trainingName").value(trainingName))
                .andExpect(jsonPath("$[0].trainingType").value(trainingType))
                .andExpect(jsonPath("$[0].trainingDateStart").value(from.format(formatter)));
    }

    @Test
    void assignTrainers_ShouldReturnTrainerList() throws Exception {
        String userName = "john123";
        List<UpdateTraineeTrainerList> assignmentList = List.of(
                new UpdateTraineeTrainerList("trainerA", 101L),
                new UpdateTraineeTrainerList("trainerB", 102L)
        );

        Trainer trainer1 = Trainer.builder()
                .firstName("Alice")
                .lastName("Smith")
                .specialization(TrainingType.builder().name("cardio").build())
                .isActive(true)
                .build();

        Trainer trainer2 = Trainer.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .specialization(TrainingType.builder().name("cardio").build())
                .isActive(true)
                .build();

        List<Trainer> trainers = List.of(trainer1, trainer2);

        when(traineeService.updateTrainer(userName, assignmentList)).thenReturn(trainers);

        TrainerWithoutTraineesResponse response1 = TrainerWithoutTraineesResponse.builder()
                .firstName("Alice")
                .lastName("Smith")
                .specialization(TrainingType.builder().name("cardio").build())
                .isActive(true)
                .build();

        TrainerWithoutTraineesResponse response2 = TrainerWithoutTraineesResponse.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .specialization(TrainingType.builder().name("cardio").build())
                .isActive(true)
                .build();

        try (MockedStatic<TrainerMapper> mockedStatic = mockStatic(TrainerMapper.class)) {
            mockedStatic.when(() -> TrainerMapper.dtoWithoutTraineeList(trainer1)).thenReturn(response1);
            mockedStatic.when(() -> TrainerMapper.dtoWithoutTraineeList(trainer2)).thenReturn(response2);

            mockMvc.perform(patch("/trainees/{userName}/trainers", userName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(assignmentList)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].firstName").value("Alice"))
                    .andExpect(jsonPath("$[1].firstName").value("Bob"));

            verify(traineeService).updateTrainer(userName, assignmentList);

            mockedStatic.verify(() -> TrainerMapper.dtoWithoutTraineeList(any(Trainer.class)), times(2));
        }
    }

    @Test
    void deleteTrainee_ShouldReturnMessageResponseAndIncrementCounter() throws Exception {
        String username = "john123";

        Counter mockCounter = mock(Counter.class);

        Counter.Builder builder = mock(Counter.Builder.class);
        when(builder.tag(anyString(), anyString())).thenReturn(builder);
        when(builder.description(anyString())).thenReturn(builder);
        when(builder.register(any())).thenReturn(mockCounter);


        try (MockedStatic<Counter> mockedStatic = Mockito.mockStatic(Counter.class)) {
            mockedStatic.when(() -> Counter.builder(anyString())).thenReturn(builder);
            mockMvc.perform(delete("/trainees/{username}", username))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Deleted trainee with username  " + username));

            verify(traineeService).delete(username);
            verify(mockCounter).increment();
        }
    }
}