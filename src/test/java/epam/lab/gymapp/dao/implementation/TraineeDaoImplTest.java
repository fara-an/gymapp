package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TraineeDaoImplTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Session session;

    @Mock
    Query<Training> typedQuery;

    private TraineeDaoImpl underTest;

    @BeforeEach
    void init() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        underTest = new TraineeDaoImpl(sessionFactory);
    }

    @Test
    void create_persistAndRefresh() {
        Trainee trainee = new Trainee();
        trainee.setId(2L);

        doNothing().when(session).persist(trainee);

        Trainee saved = underTest.create(trainee);

        assertSame(trainee, saved);

        verify(session).persist(trainee);
        verify(session).flush();
        verify(session).refresh(trainee);
    }


    @Test
    void create_throwsDaoException_whenSessionFails() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(DaoException.class, () -> underTest.create(trainee));
    }


    @Test
    void findById_returnsOptional() {
        Long id = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(id);
        when(session.get(Trainee.class, id)).thenReturn(trainee);

        Optional<Trainee> optionalTrainee = underTest.findByID(id);

        assertTrue(optionalTrainee.isPresent());
        assertEquals(trainee, optionalTrainee.get());
    }

    @Test
    void findById_notFound() {
        Long id = 21L;
        when(session.get(Trainee.class, id)).thenReturn(null);

        Optional<Trainee> optional = underTest.findByID(id);

        assertFalse(optional.isPresent());
    }

    @Test
    void findById_returnsOptional_whenSessionFails() {
        Long id = 1L;

        when(sessionFactory.getCurrentSession()).thenThrow(DaoException.class);

        assertThrows(DaoException.class, () -> underTest.findByID(id));
    }

    @Test
    void findByUsername_returnsOptional() {
        Trainee trainee = new Trainee();
        trainee.setUserName("Clementine.Kruceynski");

        Query<Trainee> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter("userName", trainee.getUserName())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(new Trainee()));

        Optional<Trainee> optionalTrainee = underTest.findByUsername(trainee.getUserName());

        assertTrue(optionalTrainee.isPresent());
        assertSame(trainee, optionalTrainee.get());
    }

    @Test
    void findByUsername_NotFound() {
        Trainee trainee = new Trainee();
        trainee.setUserName("Clementine.Kruceynski");

        Query<Trainee> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter("userName", trainee.getUserName())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Trainee> result = underTest.findByUsername(trainee.getUserName());

        assertTrue(result.isEmpty(), "Expected Optional.empty() when no entity found");
        verify(query).uniqueResultOptional();
    }

    @Test
    void findByUsername_wrapsInRuntimeException_whenFails() {
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("session failed"));

        assertThrows(DaoException.class, () -> underTest.findByUsername("Clementine"));
    }

    @Test
    void update_mergesAndRefreshes() {
        Trainee trainee = new Trainee();
        trainee.setAddress("some another address");
        when(session.merge(trainee)).thenReturn(trainee);

        Trainee updatedTrainee = underTest.update(trainee);
        assertSame(trainee, updatedTrainee);
        verify(session).merge(trainee);
        verify(session).flush();
        verify(session).refresh(trainee);
    }

    @Test
    void update_throwsDaoException_whenSessionFails() {
        Trainee trainee = Trainee.builder()
                .firstName("Ann")
                .lastName("Example")
                .userName("ann")      // field in your entity
                .build();
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(DaoException.class, () -> underTest.update(trainee));
    }

    @Test
    void delete_removesEntity() {
        Long id = 1L;
        Trainee trainee = new Trainee();
        trainee.setId(id);

        when(session.byId(Trainee.class).load(id)).thenReturn(trainee);

        underTest.delete(id);

        verify(session).remove(trainee);
    }

    @Test
    void delete_throwsDaoException_whenSessionFails() {
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(DaoException.class, () -> underTest.delete(Long.valueOf(1)));
    }

    @Test
    void getTraineeTrainings_filtersByAllParams() {

        when(session.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);
        List<Training> expected = List.of(new Training());
        when(typedQuery.getResultList()).thenReturn(expected);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);


        List<Training> actual = underTest.getTraineeTrainings(
                "ann", from, to, "coach1", "CARDIO");

        // Assert
        assertEquals(expected, actual);

        // Optional: verify that CriteriaBuilder predicates used the parameters you expect:
        verify(session.getCriteriaBuilder()).createQuery(Training.class);
    }

    @Test
    void getTraineeTrainings_handlesEmptyOptionalFields() {
        when(session.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<Training> result = underTest.getTraineeTrainings(
                "ann", null, null, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTraineeTrainings_wrapsExceptionAsDaoException() {

        assertThrows(
                DaoException.class,
                () -> underTest.getTraineeTrainings("ann", null, null, null, null)
        );
    }

}
