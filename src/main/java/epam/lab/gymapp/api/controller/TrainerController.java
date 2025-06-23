package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.response.TrainerRegistrationResponse;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<?> login(@Valid @RequestBody Credentials credentials, HttpSession session) {
       performLogin(credentials.getUsername(), credentials.getPassword(),session);
        return ResponseEntity.ok("Login successful");

    }

    @PostMapping
    public ResponseEntity<TrainerRegistrationResponse> register(@Valid @RequestBody TrainerRegistrationBody registrationDto, HttpSession session) {
        Trainer trainer = TrainerMapper.toEntity(registrationDto);
        TrainingType specialization = trainingTypeService.findByName(registrationDto.getTrainingType());
        trainer.setSpecialization(specialization);
        Trainer newTrainer = trainerService.createProfile(trainer);
        performLogin(newTrainer.getUserName(), newTrainer.getPassword(), session);
        TrainerRegistrationResponse response =
                new TrainerRegistrationResponse(newTrainer.getUserName(), newTrainer.getPassword());
        return ResponseEntity.ok(response);
    }

    private void performLogin(String username, String password, HttpSession session) {
        Credentials credentials = new Credentials(username, password);
        authenticationService.authenticateUser(credentials);
        session.setAttribute("credentials", credentials);
    }


}
