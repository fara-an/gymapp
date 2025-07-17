package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.register.TrainerRegistrationResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    public TrainerController(TrainerService trainerService, TrainingTypeService trainingTypeService) {
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
    }


    @PostMapping()
    public ResponseEntity<TrainerRegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationBody registrationDto) {
        Trainer trainer = Trainer.builder().
                firstName(registrationDto.getFirstName()).
                lastName(registrationDto.getLastName()).
                build();
        TrainingType specialization = trainingTypeService.findByName(registrationDto.getTrainingType());
        trainer.setSpecialization(specialization);
        Trainer newTrainer = trainerService.createProfile(trainer);
        return ResponseEntity.ok(TrainerMapper.dtoOnlyUsernameAndPass(newTrainer));
    }


    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainer(
            @PathVariable("username") String username) {
        Trainer trainer = trainerService.findByUsername(username);
        TrainerGetResponse entity = TrainerMapper.dtoWithTraineeList(trainer);
        return ResponseEntity.ok(entity);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainer(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateTrainerDto updateTrainerDto) {
        Trainer existingTrainer = Trainer.builder()
                .id(id)
                .firstName(updateTrainerDto.getFirstName())
                .lastName(updateTrainerDto.getLastName())
                .userName(updateTrainerDto.getUserName())
                .isActive(updateTrainerDto.isActive())
                .build();
        Trainer updatedTrainer = trainerService.updateProfile(existingTrainer);
        TrainerGetResponse entity = TrainerMapper.dtoWithTraineeList(updatedTrainer);
        return ResponseEntity.ok(entity);

    }


    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<?> getTrainersNotAssignedToTrainee(
            @PathVariable("username") String username) {
        List<Trainer> trainers = trainerService.trainersNotAssignedToTrainee(username);
        List<TrainerWithoutTraineesResponse> list = trainers.stream().map(t -> TrainerMapper.dtoWithoutTraineeList(t)).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{username}/trainings")
    public ResponseEntity<?> getTrainerTrainings(
            @PathVariable("username") String userName,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to,
            @RequestParam(value = "traineeName", required = false) String traineeName,
            @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = trainerService.getTrainerTrainings(userName, from, to, traineeName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainer(t)).toList();
        return ResponseEntity.ok(list);
    }







}
