package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TrainerWorkloadClientServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    private TrainerWorkloadClientService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TrainerWorkloadClientService(jmsTemplate, "trainerWorkload.queue");
    }

    private Training buildTraining() {
        Trainer trainer = new Trainer();
        trainer.setUserName("john.doe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setIsActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDateStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        training.setDuration(60);

        return training;
    }

    @Test
    void callToTrainerWorkloadService_ShouldSendMessageAndReturnAccepted() {
        Training training = buildTraining();

        ResponseEntity<Void> response = service.callToTrainerWorkloadService(training, "CREATE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate, times(1)).convertAndSend(eq("trainerWorkload.queue"), captor.capture());

        TrainerWorkloadRequest sentRequest = captor.getValue();
        assertThat(sentRequest.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(sentRequest.getFirstName()).isEqualTo("John");
        assertThat(sentRequest.getLastName()).isEqualTo("Doe");
        assertThat(sentRequest.getActionType()).isEqualTo("CREATE");
        assertThat(sentRequest.isActive()).isTrue();
        assertThat(sentRequest.getTrainingDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertThat(sentRequest.getDuration()).isEqualTo(60);
    }

    @Test
    void fallback_ShouldReturnServiceUnavailable() {
        Training training = buildTraining();

        ResponseEntity<Void> response = service.fallback(training, "CREATE", new RuntimeException("Test failure"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
