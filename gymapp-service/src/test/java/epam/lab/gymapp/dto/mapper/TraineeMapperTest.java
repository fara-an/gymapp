package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    @Test
    void fromDtoToTrainee_ShouldMapCorrectly() {
        
        TraineeRegistrationBody body = new TraineeRegistrationBody();
        body.setFirstName("John");
        body.setLastName("Doe");
        body.setDateOfBirth(LocalDateTime.of(1990, 5, 15, 0, 0));
        body.setAddress("123 Main St");


        Trainee trainee = TraineeMapper.fromDtoToTrainee(body);


        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals(LocalDateTime.of(1990, 5, 15, 0, 0), trainee.getBirthday());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void traineeWithTrainers_ShouldMapCorrectly() {
        
        TrainingType yoga = TrainingType.builder().name("Yoga").build();
        TrainingType cardio = TrainingType.builder().name("Cardio").build();

        Trainer trainer1 = Trainer.builder()
                .userName("trainer1")
                .firstName("Alice")
                .lastName("Smith")
                .specialization(yoga)
                .build();

        Trainer trainer2 = Trainer.builder()
                .userName("trainer2")
                .firstName("Bob")
                .lastName("Brown")
                .specialization(cardio)
                .build();

        Trainee trainee = Trainee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .birthday(LocalDateTime.of(1995, 3, 10, 0, 0))
                .address("456 Elm St")
                .isActive(true)
                .trainers(List.of(trainer1, trainer2))
                .build();


        TraineeGetResponse response = TraineeMapper.traineeWithTrainers(trainee);


        assertEquals("Jane", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals(LocalDateTime.of(1995, 3, 10, 0, 0), response.getBirthday());
        assertEquals("456 Elm St", response.getAddress());
        assertTrue(response.isActive());
        assertEquals(2, response.getTrainers().size());

        TraineeMapper.TraineeGetsResponseTrainer mappedTrainer1 = response.getTrainers().get(0);
        assertEquals("Alice", mappedTrainer1.getFirstName());
        assertEquals("Smith", mappedTrainer1.getLastName());
        assertEquals("trainer1", mappedTrainer1.getUserName());
        assertEquals("Yoga", mappedTrainer1.getSpecialization().getName());

        TraineeMapper.TraineeGetsResponseTrainer mappedTrainer2 = response.getTrainers().get(1);
        assertEquals("Bob", mappedTrainer2.getFirstName());
        assertEquals("Brown", mappedTrainer2.getLastName());
        assertEquals("trainer2", mappedTrainer2.getUserName());
        assertEquals("Cardio", mappedTrainer2.getSpecialization().getName());
    }

    @Test
    void traineeWithTrainers_ShouldHandleEmptyTrainerList() {
        
        Trainee trainee = Trainee.builder()
                .firstName("NoTrainer")
                .lastName("Person")
                .birthday(LocalDateTime.of(2000, 1, 1, 0, 0))
                .address("789 Oak St")
                .isActive(false)
                .trainers(List.of())
                .build();


        TraineeGetResponse response = TraineeMapper.traineeWithTrainers(trainee);


        assertNotNull(response.getTrainers());
        assertTrue(response.getTrainers().isEmpty());
    }
}
