package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.service.interfaces.TrainingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingAddDto trainingAddDto) {
       return trainingService.addTraining(trainingAddDto);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTraining(@RequestParam(value = "trainerUsername") String trainerUsername,
                                            @RequestParam(value = "traineeUsername") String traineeUsername,
                                            @RequestParam(value = "startTime")LocalDateTime startTime) {
        trainingService.deleteTraining(trainerUsername, traineeUsername, startTime);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<?> findTraining(@RequestParam(value = "trainerUsername") String trainerUsername,
                                          @RequestParam(value = "traineeUsername") String traineeUsername,
                                          @RequestParam(value = "startTime")LocalDateTime startTime){
        trainingService.findTraining(trainerUsername, traineeUsername,startTime);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

}
