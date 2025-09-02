package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.register.TrainerRegistrationResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    @Test
    void dtoWithTraineeList_ShouldMapCorrectly() {
        // given
        Trainee trainee1 = Trainee.builder()
                .userName("trainee1")
                .firstName("Alice")
                .lastName("Smith")
                .build();

        Trainee trainee2 = Trainee.builder()
                .userName("trainee2")
                .firstName("Bob")
                .lastName("Brown")
                .build();

        TrainingType specialization = TrainingType.builder().name("Cardio").build();

        Trainer trainer = Trainer.builder()
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .specialization(specialization)
                .trainees(List.of(trainee1, trainee2))
                .build();

        // when
        TrainerGetResponse response = TrainerMapper.dtoWithTraineeList(trainer);

        // then
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertTrue(response.isActive());
        assertEquals("Cardio", response.getSpecialization().getName());

        assertEquals(2, response.getTrainees().size());
        TrainerMapper.TraineeDto mapped1 = response.getTrainees().get(0);
        assertEquals("trainee1", mapped1.getUserName());
        assertEquals("Alice", mapped1.getFirstName());
        assertEquals("Smith", mapped1.getLastName());

        TrainerMapper.TraineeDto mapped2 = response.getTrainees().get(1);
        assertEquals("trainee2", mapped2.getUserName());
        assertEquals("Bob", mapped2.getFirstName());
        assertEquals("Brown", mapped2.getLastName());
    }

    @Test
    void dtoOnlyUsernameAndPass_ShouldMapCorrectly() {
        // given
        Trainer trainer = Trainer.builder()
                .userName("trainer123")
                .password("secret")
                .build();

        // when
        TrainerRegistrationResponse response = TrainerMapper.dtoOnlyUsernameAndPass(trainer);

        // then
        assertEquals("trainer123", response.getUsername());
        assertEquals("secret", response.getPassword());
    }

    @Test
    void dtoWithoutTraineeList_ShouldMapCorrectly() {
        // given
        TrainingType yoga = TrainingType.builder().name("Yoga").build();

        Trainer trainer = Trainer.builder()
                .firstName("Emma")
                .lastName("White")
                .isActive(false)
                .specialization(yoga)
                .build();

        // when
        TrainerWithoutTraineesResponse response = TrainerMapper.dtoWithoutTraineeList(trainer);

        // then
        assertEquals("Emma", response.getFirstName());
        assertEquals("White", response.getLastName());
        assertEquals("Yoga", response.getSpecialization().getName());
        assertFalse(response.isActive());
    }

    @Test
    void dtoWithTraineeList_ShouldHandleEmptyTraineeList() {
        // given
        TrainingType specialization = TrainingType.builder().name("Strength").build();

        Trainer trainer = Trainer.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .isActive(true)
                .specialization(specialization)
                .trainees(List.of()) // no trainees
                .build();

        // when
        TrainerGetResponse response = TrainerMapper.dtoWithTraineeList(trainer);

        // then
        assertNotNull(response.getTrainees());
        assertTrue(response.getTrainees().isEmpty());
    }
}
