package epam.lab.gymapp.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import epam.lab.gymapp.dao.interfaces.TrainingTypeDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {
    @Mock
    TrainingTypeDao<TrainingType, Long> dao;

    @InjectMocks
    TrainingTypeServiceImpl service;

    @Test
    void findByName_returnsEntity_whenPresent() {
        String name = "CARDIO";
        TrainingType expected = new TrainingType();
        when(dao.findByName(name)).thenReturn(Optional.of(expected));

        TrainingType result = service.findByName(name);

        assertSame(expected, result);
        verify(dao).findByName(name);
    }

    @Test
    void findByName_throws_whenEntityMissing() {
        String name = "UNKNOWN";
        when(dao.findByName(name)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByName(name));
        verify(dao).findByName(name);
    }
}

