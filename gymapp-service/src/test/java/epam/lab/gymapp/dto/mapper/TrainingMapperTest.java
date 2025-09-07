package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private Training createSampleTraining() {
        Trainee trainee = Trainee.builder()
                .userName("trainee123")
                .firstName("Alice")
                .lastName("Smith")
                .build();

        Trainer trainer = Trainer.builder()
                .userName("trainer456")
                .firstName("Bob")
                .lastName("Brown")
                .build();

        TrainingType trainingType = TrainingType.builder()
                .name("Cardio")
                .build();

        return Training.builder()
                .trainingName("Morning Cardio")
                .trainingType(trainingType)
                .trainingDateStart(LocalDateTime.of(2025, 9, 2, 10, 0))
                .trainee(trainee)
                .trainer(trainer)
                .build();
    }

    @Test
    void trainingWithTrainee_ShouldMapCorrectly() {
        
        Training training = createSampleTraining();


        TrainingResponse response = TrainingMapper.trainingWithTrainee(training);


        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals("Cardio", response.getTrainingType());
        assertEquals(LocalDateTime.of(2025, 9, 2, 10, 0), response.getTrainingDateStart());
        assertEquals("trainee123", response.getTraineeName());
        assertEquals("trainer456", response.getTrainerName());
    }

    @Test
    void trainingWithTrainer_ShouldMapCorrectly() {
        
        Training training = createSampleTraining();


        TrainingResponse response = TrainingMapper.trainingWithTrainer(training);


        assertEquals("Morning Cardio", response.getTrainingName());
        assertEquals("Cardio", response.getTrainingType());
        assertEquals(LocalDateTime.of(2025, 9, 2, 10, 0), response.getTrainingDateStart());
        assertEquals("trainee123", response.getTraineeName());
        assertEquals("trainer456", response.getTrainerName());
    }
}
