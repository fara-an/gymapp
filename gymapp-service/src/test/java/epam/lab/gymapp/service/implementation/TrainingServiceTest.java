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


    @Test
    @DisplayName("Should throw IllegalArgumentException when training type doesn't match trainer specialization")
    void addTraining_InvalidTrainingType() {
        trainingAddDto.setTrainingType("YOGA");

        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);

         assertThrows(
                UserInputException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

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
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(false);
        when(trainingDao.existsTrainerConflict(1L, startTime, endTime)).thenReturn(true);

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
        when(trainerService.findByUsername("trainer123"))
                .thenThrow(new RuntimeException("Trainer not found"));

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
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123"))
                .thenThrow(new RuntimeException("Trainee not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Trainee not found", exception.getMessage());
        verify(trainerService).findByUsername("trainer123");
    }

    @Test
    @DisplayName("Should handle training type service exception")
    void addTraining_TrainingTypeServiceException() {
        when(trainerService.findByUsername("trainer123")).thenReturn(trainer);
        when(traineeService.findByUsername("trainee123")).thenReturn(trainee);
        when(trainingDao.existsTraineeConflict(2L, startTime, endTime)).thenReturn(false);
        when(trainingDao.existsTrainerConflict(1L, startTime, endTime)).thenReturn(false);
        when(trainingTypeService.findByName("FITNESS"))
                .thenThrow(new RuntimeException("Training type not found"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.addTraining(trainingAddDto)
        );

        assertEquals("Training type not found", exception.getMessage());
        verify(trainingDao, never()).create(any());
    }
}