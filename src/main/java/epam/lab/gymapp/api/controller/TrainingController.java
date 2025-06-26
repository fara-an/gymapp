package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.service.interfaces.TrainingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
public class TrainingController {

    TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("/add")
    public void addTraining(@Valid @RequestBody TrainingAddDto trainingAddDto) {
        trainingService.addTraining(trainingAddDto);
    }
}
