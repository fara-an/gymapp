package epam.lab.gymapp.dao.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import epam.lab.gymapp.dao.interfaces.TrainingTypeDao;
import epam.lab.gymapp.model.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

/**
 * Unit tests for {@link TrainingTypeDaoImpl#findByName(String)}.
 *
 * <p>This class proves that the implementation correctly delegates to the default
 * method defined in {@link TrainingTypeDao} and that the interaction with
 * Hibernate works as expected. Three scenarios are covered:
 * <ol>
 *   <li>Query finds a record – {@code Optional} contains the entity.</li>
 *   <li>Query finds nothing – {@code Optional} is empty.</li>
 *   <li>Hibernate throws – the method wraps and re‑throws {@link RuntimeException}.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
class TrainingTypeDaoImplTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Query<TrainingType> query;

    private TrainingTypeDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new TrainingTypeDaoImpl(sessionFactory);
    }

    @Test
    void findByName_returnsEntity_whenPresent() {
        TrainingType trainingType = new TrainingType(); // dummy entity
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(TrainingTypeDao.FIND_BY_NAME, TrainingType.class)).thenReturn(query);
        when(query.setParameter("name", "Yoga")).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainingType));

        Optional<TrainingType> result = dao.findByName("Yoga");

        assertTrue(result.isPresent());
        assertEquals(trainingType, result.get());
        verify(query).setParameter("name", "Yoga");
    }

    @Test
    void findByName_returnsEmpty_whenNotFound() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(TrainingTypeDao.FIND_BY_NAME, TrainingType.class)).thenReturn(query);
        when(query.setParameter("name", "Pilates")).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<TrainingType> result = dao.findByName("Pilates");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByName_throwsRuntimeException_whenHibernateFails() {
        when(sessionFactory.getCurrentSession()).thenThrow(new RuntimeException("db down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.findByName("Cardio"));
        assertTrue(ex.getMessage().contains("DAO READ"));
    }
}
