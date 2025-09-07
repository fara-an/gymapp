package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TrainingDaoImplTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Query<Long> countQuery;
    @Mock
    private Query<Training> trainingQuery;
    @Mock
    private NativeQuery<Training> nativeQuery;
    @Mock
    private MutationQuery mutationQuery;

    @InjectMocks
    private TrainingDaoImpl trainingDao;

    @BeforeEach
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void existsTrainerConflict_ShouldReturnTrue_WhenConflictExists() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(eq("trainerId"), any())).thenReturn(countQuery);
        when(countQuery.setParameter(eq("newStart"), any())).thenReturn(countQuery);
        when(countQuery.setParameter(eq("newEnd"), any())).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(1L);

        boolean result = trainingDao.existsTrainerConflict(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertTrue(result);
    }

    @Test
    void existsTrainerConflict_ShouldReturnFalse_WhenNoConflict() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(0L);

        boolean result = trainingDao.existsTrainerConflict(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertFalse(result);
    }

    @Test
    void existsTraineeConflict_ShouldReturnTrue_WhenConflictExists() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(eq("traineeId"), any())).thenReturn(countQuery);
        when(countQuery.setParameter(eq("newStart"), any())).thenReturn(countQuery);
        when(countQuery.setParameter(eq("newEnd"), any())).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(2L);

        boolean result = trainingDao.existsTraineeConflict(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertTrue(result);
    }

    @Test
    void existsTraineeConflict_ShouldReturnFalse_WhenNoConflict() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.uniqueResult()).thenReturn(0L);

        boolean result = trainingDao.existsTraineeConflict(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertFalse(result);
    }

    @Test
    void findByID_ShouldReturnEntity_WhenFound() {
        Training training = new Training();
        when(session.get(eq(Training.class), eq(1L))).thenReturn(training);

        Optional<Training> result = trainingDao.findByID(1L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void findByID_ShouldReturnEmpty_WhenNotFound() {
        when(session.get(eq(Training.class), eq(1L))).thenReturn(null);

        Optional<Training> result = trainingDao.findByID(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_ShouldReturnEntity_WhenFound() {
        Training training = new Training();
        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("userName"), any())).thenReturn(trainingQuery);
        when(trainingQuery.uniqueResultOptional()).thenReturn(Optional.of(training));

        Optional<Training> result = trainingDao.findByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotFound() {
        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("userName"), any())).thenReturn(trainingQuery);
        when(trainingQuery.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Training> result = trainingDao.findByUsername("testUser");

        assertFalse(result.isPresent());
    }

    @Test
    void deleteTraining_ShouldExecuteDeleteQuery() {
        Training training = new Training();
        training.setId(99L);

        when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
        when(mutationQuery.setParameter("id", training.getId())).thenReturn(mutationQuery);

        trainingDao.deleteTraining(training);

        verify(mutationQuery).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Test
    void updateTrainingTrainer_ShouldUpdateTrainer_WhenDifferentTrainer() {
        Training training = new Training();
        Trainee trainee = new Trainee();
        trainee.setUserName("traineeUser");
        Trainer oldTrainer = new Trainer();
        oldTrainer.setUserName("oldTrainer");
        training.setTrainee(trainee);
        training.setTrainer(oldTrainer);

        Trainer newTrainer = new Trainer();
        newTrainer.setUserName("newTrainer");

        when(session.createQuery(startsWith("SELECT  tr"), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("id"), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(training));

        Query<Trainer> trainerQuery = mock(Query.class);
        when(session.createQuery(startsWith("SELECT  t"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter(eq("uname"), any())).thenReturn(trainerQuery);
        when(trainerQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(newTrainer));

        Trainer result = trainingDao.updateTrainingTrainer(1L, "traineeUser", "newTrainer");

        assertEquals(newTrainer, result);
        assertEquals(newTrainer, training.getTrainer());
    }


    @Test
    void updateTrainingTrainer_ShouldThrow_WhenTrainingNotFound() {
        when(session.createQuery(startsWith("SELECT  tr"), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("id"), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultStream()).thenReturn(java.util.stream.Stream.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingDao.updateTrainingTrainer(1L, "traineeUser", "newTrainer"));
    }

    @Test
    void updateTrainingTrainer_ShouldThrow_WhenTraineeMismatch() {
        Training training = new Training();
        Trainee trainee = new Trainee();
        trainee.setUserName("differentUser");
        training.setTrainee(trainee);

        when(session.createQuery(startsWith("SELECT  tr"), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("id"), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(training));

        assertThrows(EntityNotFoundException.class,
                () -> trainingDao.updateTrainingTrainer(1L, "expectedUser", "newTrainer"));
    }

    @Test
    void updateTrainingTrainer_ShouldThrow_WhenNewTrainerNotFound() {
        Training training = new Training();
        Trainee trainee = new Trainee();
        trainee.setUserName("traineeUser");
        Trainer oldTrainer = new Trainer();
        oldTrainer.setUserName("oldTrainer");
        training.setTrainee(trainee);
        training.setTrainer(oldTrainer);

        Trainer newTrainer = new Trainer();
        newTrainer.setUserName("newTrainer");

        when(session.createQuery(startsWith("SELECT  tr"), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("id"), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(training));

        @SuppressWarnings("unchecked")
        Query<Trainer> trainerQuery = mock(Query.class);
        when(session.createQuery(startsWith("SELECT  t"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter(eq("uname"), any())).thenReturn(trainerQuery);
        when(trainerQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(newTrainer));

        Trainer result = trainingDao.updateTrainingTrainer(1L, "traineeUser", "newTrainer");

        assertEquals(newTrainer, result);
        assertEquals(newTrainer, training.getTrainer());    }

    @SuppressWarnings("unchecked")
    @Test
    void updateTrainingTrainer_ShouldNotUpdate_WhenTrainerSame() {
        Training training = new Training();
        Trainee trainee = new Trainee();
        trainee.setUserName("traineeUser");
        Trainer sameTrainer = new Trainer();
        sameTrainer.setUserName("sameTrainer");
        training.setTrainee(trainee);
        training.setTrainer(sameTrainer);

        when(session.createQuery(startsWith("SELECT  tr"), eq(Training.class))).thenReturn(trainingQuery);
        when(trainingQuery.setParameter(eq("id"), any())).thenReturn(trainingQuery);
        when(trainingQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(training));

        Query<Trainer> trainerQuery = mock(Query.class);
        when(session.createQuery(startsWith("SELECT  t"), eq(Trainer.class))).thenReturn(trainerQuery);
        when(trainerQuery.setParameter(eq("uname"), any())).thenReturn(trainerQuery);
        when(trainerQuery.getResultStream()).thenReturn(java.util.stream.Stream.of(sameTrainer));

        Trainer result = trainingDao.updateTrainingTrainer(1L, "traineeUser", "sameTrainer");

        assertEquals(sameTrainer, result);
        assertEquals(sameTrainer, training.getTrainer());
    }

}