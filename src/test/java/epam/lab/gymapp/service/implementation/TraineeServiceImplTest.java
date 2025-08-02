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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private Session session;
    @Mock
    private PasswordEncoder passwordEncoder;

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

}
