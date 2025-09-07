package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.TrainerDao;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.utils.PasswordGenerator;
import epam.lab.gymapp.utils.UsernameGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void getTrainerTrainings_ReturnsList() {
        List<Training> trainings = List.of(new Training());
        when(trainerDao.getTrainerTrainings(anyString(), any(), any(), anyString())).thenReturn(trainings);
        List<Training> result = trainerService.getTrainerTrainings("trainer1", LocalDateTime.now(), LocalDateTime.now(), "trainee1", "type");
        assertEquals(trainings, result);
        verify(trainerDao).getTrainerTrainings(anyString(), any(), any(), anyString());
    }

    @Test
    void trainersNotAssignedToTrainee_ReturnsList() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerDao.trainersNotAssignedToTrainee(anyString())).thenReturn(trainers);
        List<Trainer> result = trainerService.trainersNotAssignedToTrainee("trainee1");
        assertEquals(trainers, result);
        verify(trainerDao).trainersNotAssignedToTrainee(anyString());
    }

    @Test
    void getDao_ReturnsTrainerDao() {
        assertEquals(trainerDao, trainerService.getDao());
    }

    @Test
    void buildProfile_ReturnsTrainerWithCorrectFields() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Yoga");
        UserProfile user = UserProfile.builder().firstName("John").lastName("Doe").userName("jdoe").password("pass").isActive(true).build();
        Trainer profile = Trainer.builder().specialization(trainingType).build();
        Trainer result = trainerService.buildProfile(user, profile);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("jdoe", result.getUserName());
        assertEquals("pass", result.getPassword());
        assertTrue(result.getIsActive());
        assertEquals(trainingType, result.getSpecialization());
    }

    @Test
    void updateProfileSpecificFields_SetsSpecialization() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Yoga");

        Trainer existing = Trainer.builder().specialization(trainingType).build();

        TrainingType trainingTypeUpdated = new TrainingType();
        trainingTypeUpdated.setName("Jui Jitsi");

        Trainer item = Trainer.builder().specialization(trainingTypeUpdated).build();
        trainerService.updateProfileSpecificFields(existing, item);

        assertEquals(trainingTypeUpdated, existing.getSpecialization());
    }

    @Test
    void createProfile_CreatesAndReturnsTrainer() {
        try (MockedStatic<UsernameGenerator> usernameGen = Mockito.mockStatic(UsernameGenerator.class);
             MockedStatic<PasswordGenerator> passwordGen = Mockito.mockStatic(PasswordGenerator.class)) {

            Trainer trainer = Trainer.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .userName("jsmith")
                    .build();

            Trainer createdTrainer = Trainer.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .userName("jsmith")
                    .build();


            String generatedPassword = "pass123";
            String encodedPassword = "encodedPass123";
            usernameGen.when(() -> UsernameGenerator.generateUsername(anyString(), anyString(), any())).thenReturn("jsmith");
            passwordGen.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);

            when(passwordEncoder.encode(generatedPassword)).thenReturn(encodedPassword);

            when(trainerDao.create(any(Trainer.class))).thenReturn(createdTrainer);
            Trainer result = trainerService.createProfile(trainer);
            assertEquals(createdTrainer, result);
        }
    }

    @Test
    void updateProfile_UpdatesAndReturnsTrainer() {
        Trainer item = Trainer.builder().firstName("John").lastName("Doe").isActive(true).build();
        Trainer existing = Trainer.builder().firstName("Old").lastName("Name").isActive(false).build();
        when(trainerDao.findByID(any())).thenReturn(Optional.of(existing));
        when(trainerDao.update(any(Trainer.class))).thenReturn(existing);
        Trainer result = trainerService.updateProfile(item);
        assertEquals(existing, result);
        assertEquals("John", existing.getFirstName());
        assertEquals("Doe", existing.getLastName());
        assertTrue(existing.getIsActive());
    }

    @Test
    void findByUsername_ReturnsTrainer() {
        Trainer trainer = Trainer.builder().userName("jsmith").build();
        when(trainerDao.findByUsername("jsmith")).thenReturn(Optional.of(trainer));
        Trainer result = trainerService.findByUsername("jsmith");
        assertEquals(trainer, result);
    }

    @Test
    void findByUsername_ThrowsIfNotFound() {
        when(trainerDao.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainerService.findByUsername("notfound"));
    }

    @Test
    void findById_ReturnsTrainer() {
        Trainer trainer = Trainer.builder().build();
        when(trainerDao.findByID(anyLong())).thenReturn(Optional.of(trainer));
        Trainer result = trainerService.findById(1L);
        assertEquals(trainer, result);
    }

    @Test
    void findById_ThrowsIfNotFound() {
        when(trainerDao.findByID(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trainerService.findById(1L));
    }




}
