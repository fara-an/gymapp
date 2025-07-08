package epam.lab.gymapp.service.implementation;


import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.utils.PasswordGenerator;
import epam.lab.gymapp.utils.UsernameGenerator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private SessionFactory sessionFactory;
    @Mock
    private Session session;        // returned by sessionFactory

    @InjectMocks
    private TraineeServiceImpl underTest;

    private Trainee johnDoe;

    @BeforeEach
    void setUp() {
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

        // every DAO call that needs the Hibernate session
//        when(traineeDao.getSessionFactory()).thenReturn(sessionFactory);
//        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    // ===─┤ createProfile ├─===================================================
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

            ug.when(() -> UsernameGenerator.generateUsername(eq("John"), eq("Doe"), any()))
                    .thenReturn(generatedUsername);
            pg.when(PasswordGenerator::generatePassword).thenReturn(generatedPassword);


            when(traineeDao.create(any(Trainee.class)))
                    .thenAnswer(inv -> inv.getArgument(0, Trainee.class));

            Trainee result = underTest.createProfile(input);

            assertThat(result.getUserName()).isEqualTo(generatedUsername);
            assertThat(result.getPassword()).isEqualTo(generatedPassword);
            assertThat(result.getIsActive()).isTrue();
            verify(traineeDao).create(result);
        }
    }

    // ===─┤ updateProfile ├─===================================================
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

    // ===─┤ findByUsername ├─==================================================
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

    // ===─┤ findById ├─========================================================
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

    // ===─┤ toggleActiveStatus ├─=============================================
//    @Test
//    void toggleActiveStatus_shouldFlipIsActiveAndMerge() {
//        when(traineeDao.getSessionFactory()).thenReturn(sessionFactory);
//        when(sessionFactory.getCurrentSession()).thenReturn(session);
//        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));
//
//        underTest.toggleActiveStatus("john.doe");
//
//        assertThat(johnDoe.getIsActive()).isFalse();
//        verify(session).merge(johnDoe);
//    }
//
//    @Test
//    void toggleActiveStatus_shouldThrowWhenAbsent() {
//        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> underTest.toggleActiveStatus("ghost"))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    // ===─┤ changePassword ├─==================================================
//    @Test
//    void changePassword_whenOldMatches_shouldUpdateAndReturnNew() {
//        when(traineeDao.getSessionFactory()).thenReturn(sessionFactory);
//        when(sessionFactory.getCurrentSession()).thenReturn(session);
//        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));
//
//        boolean result = underTest.changePassword("john.doe", "oldPass", "newPass");
//
//        assertTrue(result);
//        assertThat(johnDoe.getPassword()).isEqualTo("newPass");
//        verify(session).merge(johnDoe);
//    }
//
//    @Test
//    void changePassword_shouldThrowWhenUserAbsent() {
//        when(traineeDao.findByUsername("ghost")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() ->
//                underTest.changePassword("ghost", "irrelevant", "newPass"))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//
//    @Test
//    void changePassword_whenOldDoesNotMatch_shouldDoNothing() {
//        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));
//
//        boolean result = underTest.changePassword("john.doe", "wrongOld", "newPass");
//
//        assertFalse(result);
//        assertThat(johnDoe.getPassword()).isEqualTo("oldPass"); // not changed
//        verify(session, never()).merge(any());
//    }
//
//    // ===─┤ getTraineeTrainings ├─============================================
//    @Test
//    void getTraineeTrainings_shouldDelegateToDao() {
//        List<Training> trainings = List.of(new Training(), new Training());
//        when(traineeDao.getTraineeTrainings(
//                eq("john.doe"), any(), any(), any(), any()))
//                .thenReturn(trainings);
//
//        List<Training> result = underTest.getTraineeTrainings(
//                "john.doe",
//                LocalDateTime.now().minusDays(30),
//                LocalDateTime.now(),
//                null, null);
//
//        assertThat(result).hasSize(2);
//        verify(traineeDao).getTraineeTrainings(anyString(), any(), any(), any(), any());
//    }
//
//    // ===─┤ delete ├─==========================================================
//    @Test
//    void delete_shouldResolveIdAndDelegateToDao() {
//        when(traineeDao.findByUsername("john.doe")).thenReturn(Optional.of(johnDoe));
//
//        underTest.delete("john.doe");
//
//        verify(traineeDao).delete(1L);
//    }
}
