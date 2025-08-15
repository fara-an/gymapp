package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import epam.lab.gymapp.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TrainerWorkloadClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadClientService.class);
    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;

    public TrainerWorkloadClientService(RestTemplate restTemplate, LoadBalancerClient loadBalancerClient) {
        this.restTemplate = restTemplate;
        this.loadBalancerClient = loadBalancerClient;
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


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TrainerWorkloadRequest> request = new HttpEntity<>(trainerWorkloadRequest, headers);
        ServiceInstance serviceInstance = loadBalancerClient.choose("TRAINERWORKLOADSERVICE");
        if (serviceInstance == null) {
            throw new RuntimeException("TRAINERWORKLOADSERVICE is not registered in service registry");
        }
        String uri = serviceInstance.getUri().toString();
        String contextPath = serviceInstance.getMetadata().get("contextPath");
        LOGGER.debug("TRAINERWORKLOADSERVICE uri:{}, contextPath:{}", uri, contextPath);
        return restTemplate.postForEntity(uri + contextPath + "/trainer-workloads", request, Void.class);
    }

    public ResponseEntity<Void> fallback(Training training, String actionType, Throwable ex) {
        LOGGER.warn("TRAINERWORKLOADSERVICE call failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }


}
