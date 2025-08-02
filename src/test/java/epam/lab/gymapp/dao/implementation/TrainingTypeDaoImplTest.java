package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeDaoImplTest {
    @Mock
    SessionFactory sessionFactory;

    @Mock
    Session session;

    @Mock
    Query<TrainingType> query;


    @InjectMocks
    TrainingTypeDaoImpl underTest;

    @BeforeEach
    void setUp() {
        // ‑‑‑ abstract helpers wired with stubs
        when(underTest.getSessionFactory()).thenReturn(sessionFactory);
        when(underTest.getEntityClass()).thenReturn(TrainingType.class);

        // ‑‑‑ common Hibernate plumbing
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createQuery(anyString(), eq(TrainingType.class))).thenReturn(query);

    }

    @Test
    void testFindByName_success() {
        TrainingType expected = new TrainingType();
        when(query.setParameter("name", "yoga")).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(expected));

        // act
        Optional<TrainingType> result = underTest.findByName("yoga");

        // assert
        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(query).setParameter("name", "yoga");   // extra: interaction check

    }

    @Test
    void returnsEmptyOptional_whenNameDoesNotExist() {
        // arrange
        when(query.setParameter("name", "boxing")).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        // act
        Optional<TrainingType> result = underTest.findByName("boxing");

        // assert
        assertTrue(result.isEmpty());
    }


    @Test
    void wrapsUnderlyingException_inRuntimeException() {
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenThrow(new IllegalStateException("boom"));

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> underTest.findByName("fails"));

        assertTrue(ex.getMessage().contains("DAO READ - Failed to read entity"));
        // optional: assert cause
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }

}
