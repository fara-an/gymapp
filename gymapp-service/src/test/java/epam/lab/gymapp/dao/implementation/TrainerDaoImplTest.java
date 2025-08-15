package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.model.*;
import epam.lab.gymapp.model.Trainer;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDaoImplTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Session session;

    @Mock
    Query<Training> typedQuery;

    @InjectMocks
    TrainerDaoIMpl underTest;

    @BeforeEach
    void init() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        underTest = new TrainerDaoIMpl(sessionFactory);
    }

    @Test
    void create() {
        Trainer trainer = new Trainer();
        trainer.setId(2L);

        doNothing().when(session).persist(trainer);

        Trainer saved = underTest.create(trainer);

        assertSame(saved, trainer);

        verify(session).persist(trainer);
        verify(session).flush();
        verify(session).refresh(trainer);
    }

    @Test
    void create_throwsDaoException_whenSessionFails() {

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session failed"));

        assertThrows(DaoException.class, () -> underTest.create(trainer));

    }

    @Test
    void findById_returnsOptional() {
        Long id = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(id);

        when(session.get(Trainer.class, id)).thenReturn(trainer);

        Optional<Trainer> optionalTrainer = underTest.findByID(id);
        assertTrue(optionalTrainer.isPresent());
        assertEquals(trainer, optionalTrainer.get());
    }

    @Test
    void findById_notFound(){
        Long id= 21L;
        when(session.get(Trainer.class,id)).thenReturn(null);

        Optional<Trainer> optional = underTest.findByID(id);

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
        Trainer trainer = new Trainer();
        trainer.setUserName("Clementine.Kruceynski");
        Query<Trainer> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("userName", trainer.getUserName())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainer));

        Optional<Trainer> optionalTrainer = underTest.findByUsername(trainer.getUserName());

        assertTrue(optionalTrainer.isPresent());
        assertSame(trainer, optionalTrainer.get());
    }

    @Test
    void findByUsername_NotFound(){
        Trainer trainer = new Trainer();
        trainer.setUserName("Clementine.Kruceynski");

        Query<Trainer> query = mock(Query.class);
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter("userName", trainer.getUserName())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Trainer> result = underTest.findByUsername(trainer.getUserName());

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
        Trainer trainer = new Trainer();
        trainer.setFirstName("Clementine");
        when(session.merge(trainer)).thenReturn(trainer);

        Trainer updatedTrainer = underTest.update(trainer);
        assertSame(trainer, updatedTrainer);
        verify(session).merge(trainer);
        verify(session).flush();
        verify(session).refresh(trainer);
    }

    @Test
    void update_throwsDaoException_whenSessionFails() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Clementine");
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(DaoException.class, () -> underTest.update(trainer));
    }

    @Test
    void delete_removesEntity() {
        Long id = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(id);

        when(session.byId(Trainer.class).load(id)).thenReturn(trainer);

        underTest.delete(id);

        verify(session).remove(trainer);
    }

    @Test
    void delete_throwsDaoException_whenSessionFails() {
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(DaoException.class, () -> underTest.delete(Long.valueOf(1)));
    }

    @Test
    void getTrainerTrainings_filtersByAllParams() {
        when(session.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);
        List<Training> expected = List.of(new Training());
        when(typedQuery.getResultList()).thenReturn(expected);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);


        List<Training> actual = underTest.getTrainerTrainings(
                "trainer", from, to, "trainee");

        assertEquals(expected, actual);

        verify(session.getCriteriaBuilder()).createQuery(Training.class);

    }

    @Test
    void getTrainerTrainings_handlesEmptyOptionalFields(){
        when(session.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of());

        List<Training> result = underTest.getTrainerTrainings(
                "trainer", null, null, "trainee");

        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainerTrainings_wrapsExceptionAsDaoException() {
        when(sessionFactory.getCurrentSession()).thenThrow(new DaoException("Session Failed"));

        assertThrows(
                DaoException.class,
                () -> underTest.getTrainerTrainings("ann", null, null, null)
        );
    }

}
