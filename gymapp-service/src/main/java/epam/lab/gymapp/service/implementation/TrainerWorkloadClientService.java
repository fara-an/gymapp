package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import epam.lab.gymapp.model.Training;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
public class TrainerWorkloadClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadClientService.class);
    private final String destinationOfQueue;
    private final SqsTemplate sqsTemplate;

    public TrainerWorkloadClientService(SqsTemplate sqsTemplate, @Value("${cloud.aws.queue.name}") String destinationOfQueue) {
        this.sqsTemplate = sqsTemplate;
        this.destinationOfQueue=destinationOfQueue;
    }

    @CircuitBreaker(name = "trainerWorkloadCB", fallbackMethod = "fallback")
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
        sqsTemplate.send(sqsSendOptions -> sqsSendOptions.queue(destinationOfQueue).payload(trainerWorkloadRequest));
        LOGGER.debug("Sent message to queue {}: trainerUsername={}, actionType={}, trainingDate={}, duration={}",
                destinationOfQueue,
                trainerWorkloadRequest.getTrainerUsername(),
                trainerWorkloadRequest.getActionType(),
                trainerWorkloadRequest.getTrainingDate(),
                trainerWorkloadRequest.getDuration());
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<Void> fallback(Training training, String actionType, Throwable ex) {
        LOGGER.warn("TRAINERWORKLOADSERVICE call failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }


}
