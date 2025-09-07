package epam.lab.gymapp.service.implementation;


import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.utils.PasswordGenerator;
import epam.lab.gymapp.utils.UsernameGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TrainingDao trainingDao;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl underTest;

    private Trainee johnDoe;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .id(100L)
                .userName("trainer.1")
                .firstName("Mike")
                .lastName("Trainer")
                .build();

        training = Training.builder()
                .id(200L)
                .trainer(trainer)
                .build();

        johnDoe = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe")
                .password("oldPass")
                .isActive(true)
                .birthday(LocalDate.of(1990, 1, 1).atStartOfDay())
                .address("Street 1")
                .build();


    }

    @Test
    void delete_shouldRemoveTraineeByUsername() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));

        underTest.delete("john.doe");

        verify(traineeDao).delete(1L);
    }

    @Test
    void delete_shouldThrowWhenTraineeNotFound() {
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.delete("missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        List<Training> trainings = List.of(training);
        when(traineeDao.getTraineeTrainings(eq("john.doe"), any(), any(), any(), any()))
                .thenReturn(trainings);

        List<Training> result = underTest.getTraineeTrainings(
                "john.doe",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                "Mike Trainer",
                "Yoga"
        );

        assertThat(result).containsExactly(training);
    }

    @Test
    void updateTrainer_shouldUpdateTrainerOnTraining() {
        Trainer oldTrainer = Trainer.builder().id(10L).userName("old.trainer").build();
        Training t = Training.builder().id(200L).trainer(oldTrainer).build();

        Trainee trainee = Trainee.builder().id(1L).userName("john.doe").build();
        trainee.setTrainings(new ArrayList<>(List.of(t)));

        Trainer newTrainer = Trainer.builder().id(11L).userName("new.trainer").build();

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(trainerDao.findByUsername("new.trainer")).thenReturn(Optional.of(newTrainer));
        when(trainingDao.update(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Trainer> trainers = underTest.updateTrainer(
                "john.doe",
                List.of(new UpdateTraineeTrainerList("new.trainer", 200L))
        );

        assertThat(trainers).containsExactly(newTrainer);

        ArgumentCaptor<Training> cap = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).update(cap.capture());
        assertThat(cap.getValue().getId()).isEqualTo(200L);
        assertThat(cap.getValue().getTrainer()).isEqualTo(newTrainer);
    }

    @Test
    void updateTrainer_shouldThrowWhenTraineeNotFound() {
        when(traineeDao.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateTrainer("missing", List.of()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateTrainer_shouldThrowWhenTrainingNotBelongToTrainee() {
        Trainer oldTrainer = Trainer.builder().id(10L).userName("old.trainer").build();
        Training t = Training.builder().id(200L).trainer(oldTrainer).build();
        johnDoe.setTrainings(List.of(t));

        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));

        assertThatThrownBy(() -> underTest.updateTrainer(
                "john.doe",
                List.of(new epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList("trainer.1", 999L))
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateTrainer_shouldThrowWhenTrainerNotFound() {Trainer oldTrainer = Trainer.builder().id(10L).userName("old.trainer").build();
        Training t = Training.builder().id(200L).trainer(oldTrainer).build();
        johnDoe.setTrainings(List.of(t));
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));
        when(trainerDao.findByUsername("ghost.trainer")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.updateTrainer(
                "john.doe",
                List.of(new epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList("ghost.trainer", 200L))
        )).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void buildProfile_shouldCopyUserAndProfileFields() {
        UserProfile user = UserProfile.builder()
                .firstName("Jane")
                .lastName("Smith")
                .userName("jane.smith")
                .password("pwd")
                .isActive(true)
                .build();

        Trainee profile = Trainee.builder()
                .birthday(LocalDate.of(2000, 1, 1).atStartOfDay())
                .address("Street X")
                .build();

        Trainee result = underTest.buildProfile(user, profile);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getBirthday()).isEqualTo(profile.getBirthday());
        assertThat(result.getAddress()).isEqualTo("Street X");
    }

    @Test
    void updateProfileSpecificFields_shouldApplyNonNullFields() {
        Trainee patch = Trainee.builder()
                .birthday(LocalDate.of(1985, 5, 5).atStartOfDay())
                .address("New Address")
                .build();

        underTest.updateProfileSpecificFields(johnDoe, patch);

        assertThat(johnDoe.getBirthday()).isEqualTo(patch.getBirthday());
        assertThat(johnDoe.getAddress()).isEqualTo("New Address");
    }

    @Test
    void updateProfileSpecificFields_shouldSkipNullFields() {
        LocalDateTime originalBirthday = johnDoe.getBirthday();
        String originalAddress = johnDoe.getAddress();

        Trainee patch = Trainee.builder().build();

        underTest.updateProfileSpecificFields(johnDoe, patch);

        assertThat(johnDoe.getBirthday()).isEqualTo(originalBirthday);
        assertThat(johnDoe.getAddress()).isEqualTo(originalAddress);
    }



@Test
    void createProfile_shouldBuildAndPersistNewTrainee() {
        Trainee input = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDate.of(1990, 1, 1).atStartOfDay())
                .address("Street 1")
                .build();

        try (MockedStatic<UsernameGenerator> ug = mockStatic(UsernameGenerator.class);
             MockedStatic<PasswordGenerator> pg = mockStatic(PasswordGenerator.class)) {

            String generatedUsername = "john.doe";
            String generatedPassword = "pass123";
            String encodedPassword = "encodedPass123";

            ug.when(() -> UsernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                    .thenReturn(generatedUsername);
            pg.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);

            when(traineeDao.create(any(Trainee.class)))
                    .thenAnswer(inv -> inv.getArgument(0, Trainee.class));

            Trainee result = underTest.createProfile(input);

            assertThat(result.getUserName()).isEqualTo(generatedUsername);
            assertThat(result.getPassword()).isEqualTo(encodedPassword);
            assertThat(result.getIsActive()).isTrue();
            verify(traineeDao).create(result);
        }
    }

    @Test
    void updateProfile_shouldMergeEditableFields() {
        when(traineeDao.findByID(1L)).thenReturn(Optional.of(johnDoe));
        when(traineeDao.update(any(Trainee.class)))
                .thenAnswer(inv -> inv.getArgument(0, Trainee.class));

        Trainee patch = Trainee.builder()
                .id(1L)
                .firstName("Johnny")
                .address("New Address")
                .isActive(false)
                .build();

        Trainee updated = underTest.updateProfile(patch);

        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getAddress()).isEqualTo("New Address");
        assertThat(updated.getIsActive()).isFalse();
        verify(traineeDao).update(johnDoe);
    }

    @Test
    void findByUsername_shouldReturnTraineeWhenExists() {
        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));

        Trainee found = underTest.findByUsername("john.doe");

        assertThat(found).isSameAs(johnDoe);
    }

    @Test
    void findByUsername_shouldThrowWhenAbsent() {
        when(traineeDao.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findByUsername("unknown"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findById_shouldReturnTraineeWhenExists() {
        when(traineeDao.findByID(1L)).thenReturn(Optional.of(johnDoe));

        assertThat(underTest.findById(1L)).isSameAs(johnDoe);
    }

    @Test
    void findById_shouldThrowWhenAbsent() {
        when(traineeDao.findByID(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.findById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
