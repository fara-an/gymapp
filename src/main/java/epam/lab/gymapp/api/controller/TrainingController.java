package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.service.interfaces.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

    TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/")
    public ResponseEntity<?> addTraining(@Valid @RequestBody TrainingAddDto trainingAddDto) {
        trainingService.addTraining(trainingAddDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
