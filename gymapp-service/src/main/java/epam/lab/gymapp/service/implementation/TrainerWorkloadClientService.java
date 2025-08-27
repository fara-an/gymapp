package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import epam.lab.gymapp.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrainerWorkloadClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadClientService.class);
    private final JmsTemplate jmsTemplate;
    @Value("${queue.trainerWorkload}")
    private String destinationOfQueue;

    public TrainerWorkloadClientService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "trainerWorkloadCB", fallbackMethod = "fallback")
    public ResponseEntity<Void> callToTrainerWorkloadService(Training training, String actionType) {
        TrainerWorkloadRequest trainerWorkloadRequest = TrainerWorkloadRequest.
                builder().
                trainerUsername(training.getTrainer().getUserName()).
                firstName(training.getTrainer().getFirstName()).
                lastName(training.getTrainer().getLastName()).
                actionType(actionType).
                isActive(training.getTrainer().getIsActive()).
                trainingDate(training.getTrainingDateStart()).
                duration(training.getDuration()).
                build();
        jmsTemplate.convertAndSend(destinationOfQueue,trainerWorkloadRequest);
        LOGGER.debug("Message is sent to the queue");
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<Void> fallback(Training training, String actionType, Throwable ex) {
        LOGGER.warn("TRAINERWORKLOADSERVICE call failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }


}
