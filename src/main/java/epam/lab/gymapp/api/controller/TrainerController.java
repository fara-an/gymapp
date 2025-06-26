package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.register.TrainerRegistrationResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainer")
public class TrainerController {

    private TrainerService trainerService;
    private TrainingTypeService trainingTypeService;
    private AuthenticationService authenticationService;

    public TrainerController(TrainerService trainerService, TrainingTypeService trainingTypeService, AuthenticationService authenticationService) {
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Credentials credentials, HttpSession session) {
        performLogin(credentials.getUsername(), credentials.getPassword(), session);
        return ResponseEntity.ok("Login successful");

    }

    @PostMapping("/register")
    public ResponseEntity<TrainerRegistrationResponse> register(@Valid @RequestBody TrainerRegistrationBody registrationDto, HttpSession session) {
        Trainer trainer = Trainer.builder().
                firstName(registrationDto.getFirstName()).
                lastName(registrationDto.getLastName()).
                build();
        TrainingType specialization = trainingTypeService.findByName(registrationDto.getTrainingType());
        trainer.setSpecialization(specialization);
        Trainer newTrainer = trainerService.createProfile(trainer);
        performLogin(newTrainer.getUserName(), newTrainer.getPassword(), session);
        return ResponseEntity.ok(TrainerMapper.dtoOnlyUsernameAndPass(trainer));
    }


    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        trainerService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok("Changes the password successfully");
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainer(@PathVariable("username") String username) {
        Trainer trainer = trainerService.findByUsername(username);
        TrainerGetResponse entity = TrainerMapper.dtoWithTraineeList(trainer);
        return ResponseEntity.ok(entity);

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTrainer(@PathVariable("id") Long id, @Valid @RequestBody UpdateTrainerDto updateTrainerDto) {
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

    @GetMapping("/notAssignedToTrainee/{username}")
    public ResponseEntity<?> getTrainersNotAssignedToTrainee(@PathVariable("username") String username) {
        List<Trainer> trainers = trainerService.trainersNotAssignedToTrainee(username);
        List<TrainerWithoutTraineesResponse> list = trainers.stream().map(t -> TrainerMapper.dtoWithoutTraineeList(t)).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getTrainings")
    public ResponseEntity<?> getTraineeTrainings(@RequestParam("userName") String userName,
                                                 @RequestParam(value = "from", required = false) LocalDateTime from,
                                                 @RequestParam(value = "to", required = false) LocalDateTime to,
                                                 @RequestParam(value = "trainerName", required = false) String trainerName,
                                                 @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = trainerService.getTrainerTrainings(userName, from, to, trainerName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainer(t)).toList();
        return ResponseEntity.ok(list);
    }

    private void performLogin(String username, String password, HttpSession session) {
        Credentials credentials = new Credentials(username, password);
        authenticationService.authenticateUser(credentials);
        session.setAttribute("credentials", credentials);
    }


}
