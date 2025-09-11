package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.service.implementation.TrainerWorkloadClientService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;

@TestConfiguration
public class TrainerWorkloadClientServiceStubConfig {

    @Bean
    TrainerWorkloadClientService trainerWorkloadClientService() {
        return new TrainerWorkloadClientService(null, "test-queue") {
            @Override
            public ResponseEntity<Void> callToTrainerWorkloadService(Training training, String action) {
                if (training.getTrainingName().equals("force-fail")){
                    return ResponseEntity.internalServerError().build();
                }
                return ResponseEntity.accepted().build();
            }
        };

    }
}

