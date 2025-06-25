package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.mapper.TrainerGetResponseMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.register.TrainerRegistrationResponse;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Trainer trainer = TrainerMapper.toEntity(registrationDto);
        TrainingType specialization = trainingTypeService.findByName(registrationDto.getTrainingType());
        trainer.setSpecialization(specialization);
        Trainer newTrainer = trainerService.createProfile(trainer);
        performLogin(newTrainer.getUserName(), newTrainer.getPassword(), session);
        return ResponseEntity.ok(TrainerGetResponseMapper.dtoOnlyUsernameAndPass(trainer));
    }


    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        trainerService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok("Changes the password successfully");
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainer(@PathVariable("username") String username) {
        Trainer trainer = trainerService.findByUsername(username);
        TrainerGetResponse entity = TrainerGetResponseMapper.dtoWithTraineeList(trainer);
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
        TrainerGetResponse entity = TrainerGetResponseMapper.dtoWithTraineeList(updatedTrainer);
        return ResponseEntity.ok(entity);

    }

    @GetMapping("/notAssignedToTrainee/{username}")
    public ResponseEntity<?> getTrainersNotAssignedToTrainee(@PathVariable("username") String username) {
        List<Trainer> trainers = trainerService.trainersNotAssignedToTrainee(username);
        List<TrainerWithoutTraineesResponse> list = trainers.stream().map(t -> TrainerGetResponseMapper.dtoWithoutTraineeList(t)).toList();
        return ResponseEntity.ok(list);
    }

    private void performLogin(String username, String password, HttpSession session) {
        Credentials credentials = new Credentials(username, password);
        authenticationService.authenticateUser(credentials);
        session.setAttribute("credentials", credentials);
    }


}
