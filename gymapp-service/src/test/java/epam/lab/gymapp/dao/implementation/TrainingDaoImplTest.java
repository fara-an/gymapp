package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.Training;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}