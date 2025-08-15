package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.exceptions.UserInputException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainerWorkloadClientService trainerWorkloadClientService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingAddDto trainingAddDto;
    private Trainer trainer;
    private Trainee trainee;
    private TrainingType trainingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        endTime = startTime.plusMinutes(60);

        TrainingType specialization = new TrainingType();
        specialization.setName("FITNESS");

        trainer = Trainer.builder()
                .id(1L)
                .userName("trainer123")
                .specialization(specialization)
                .build();

        trainee = Trainee.builder()
                .id(2L)
                .userName("trainee123")
                .build();

        trainingType = new TrainingType();
        trainingType.setName("FITNESS");


        trainingAddDto = TrainingAddDto.builder()
                .trainingName("Morning Workout")
                .trainerUserName("trainer123")
                .traineeUserName("trainee123")
                .trainingType("FITNESS")
                .trainingDateStart(startTime)
                .duration(60)
                .build();
    }

//    @Test
//    @DisplayName("Should successfully create training when all conditions are met")
//    void addTraining_Success() {
//        Training createdTraining = Training.builder()
//                .id(1L)
//                .trainee(trainee)
//                .trainer(trainer)
//                .trainingName("Morning Workout")
//                .duration(60)
//                .trainingDateStart(startTime)
//                .trainingDateEnd(endTime)
//                .trainingType(trainingType)
//                .build();
//
//        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
//        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
//        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(false);
//        when(trainingDao.existsTrainerConflict(1L, startTime, endTime)).thenReturn(false);
//        when(trainingTypeService.findByName("FITNESS")).thenReturn(trainingType);
//        when(trainingDao.create(any(Training.class))).thenReturn(createdTraining);
//
//        ResponseEntity<Training> result = (ResponseEntity<Training>) trainingService.addTraining(trainingAddDto);
//
//        assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode()); // ✅ status code check
//        assertNotNull(result.getBody()); // ✅ ensure body is present
//
//        Training body = result.getBody();
//        assertNotNull(result);
//        assertEquals(1L, body.getId());
//        assertEquals("Morning Workout", body.getTrainingName());
//        assertEquals(trainer, body.getTrainer());
//        assertEquals(trainee, body.getTrainee());
//        assertEquals(startTime, body.getTrainingDateStart());
//        assertEquals(endTime, body.getTrainingDateEnd());
//        assertEquals(60, body.getDuration());
//        assertEquals(trainingType, body.getTrainingType());
//
//        verify(trainerService).findByUsername("trainer123");
//        verify(traineeService).findByUsername("trainee123");
//        verify(trainingDao).existsTraineeConflict(2L, startTime, endTime);
//        verify(trainingDao).existsTrainerConflict(1L, startTime, endTime);
//        verify(trainingTypeService).findByName("FITNESS");
//        verify(trainingDao).create(any(Training.class));
//    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when training type doesn't match trainer specialization")
    void addTraining_InvalidTrainingType() {
        // Given
        trainingAddDto.setTrainingType("YOGA");

        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);

        // When & Then
         assertThrows(
                UserInputException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        // Verify that no conflict checks or creation happens after validation failure
        verify(trainingDao, never()).existsTraineeConflict(anyLong(), any(), any());
        verify(trainingDao, never()).existsTrainerConflict(anyLong(), any(), any());
        verify(trainingDao, never()).create(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trainee has conflicting session")
    void addTraining_TraineeConflict() {

        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(true);

        UserInputException exception = assertThrows(
                UserInputException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Trainee already has a session that overlaps with this time window",
                exception.getMessage());

        verify(trainingDao).existsTraineeConflict(2L, startTime, endTime);
        verify(trainingDao, never()).existsTrainerConflict(anyLong(), any(), any());
        verify(trainingDao, never()).create(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when trainer has conflicting session")
    void addTraining_TrainerConflict() {
        // Given
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(false);
        when(trainingDao.existsTrainerConflict(1L, startTime, endTime)).thenReturn(true);

        // When & Then
        UserInputException exception = assertThrows(
                UserInputException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Trainer already has a session that overlaps with this time window",
                exception.getMessage());

        verify(trainingDao).existsTraineeConflict(2L, startTime, endTime);
        verify(trainingDao).existsTrainerConflict(1L, startTime, endTime);
        verify(trainingDao, never()).create(any());
    }

    @Test
    @DisplayName("Should handle trainer not found exception")
    void addTraining_TrainerNotFound() {
        // Given
        when(trainerService.findByUsername("trainer123"))
                .thenThrow(new RuntimeException("Trainer not found"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Trainer not found", exception.getMessage());
        verify(traineeService, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Should handle trainee not found exception")
    void addTraining_TraineeNotFound() {
        // Given
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123"))
                .thenThrow(new RuntimeException("Trainee not found"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Trainee not found", exception.getMessage());
        verify(trainerService).findByUsername("trainer123");
    }
//
//    @Test
//    @DisplayName("Should calculate correct end time based on duration")
//    void addTraining_CorrectEndTimeCalculation() {
//        // Given
//        trainingAddDto.setDuration(90); // 90 minutes
//        LocalDateTime expectedEndTime = startTime.plusMinutes(90);
//
//        Training createdTraining = Training.builder()
//                .id(1L)
//                .trainee(trainee)
//                .trainer(trainer)
//                .trainingName("Morning Workout")
//                .duration(90)
//                .trainingDateStart(startTime)
//                .trainingDateEnd(expectedEndTime)
//                .trainingType(trainingType)
//                .build();
//
//        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
//        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
//        when(trainingDao.existsTraineeConflict(2L, startTime, expectedEndTime)).thenReturn(false);
//        when(trainingDao.existsTrainerConflict(1L, startTime, expectedEndTime)).thenReturn(false);
//        when(trainingTypeService.findByName("FITNESS")).thenReturn(trainingType);
//        when(trainingDao.create(any(Training.class))).thenReturn(createdTraining);
//
//        // When
//        ResponseEntity<Training> result = (ResponseEntity<Training>) trainingService.addTraining(trainingAddDto);
//        Training body = result.getBody();
//
//        // Then
//        assertEquals(expectedEndTime, body.getTrainingDateEnd());
//        assertEquals(90, body.getDuration());
//
//        verify(trainingDao).existsTraineeConflict(2L, startTime, expectedEndTime);
//        verify(trainingDao).existsTrainerConflict(1L, startTime, expectedEndTime);
//    }

    @Test
    @DisplayName("Should handle training type service exception")
    void addTraining_TrainingTypeServiceException() {
        // Given
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(false);
        when(trainingDao.existsTrainerConflict(1L, startTime, endTime)).thenReturn(false);
        when(trainingTypeService.findByName("FITNESS"))
                .thenThrow(new RuntimeException("Training type not found"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingDao, never()).create(any());
    }
}