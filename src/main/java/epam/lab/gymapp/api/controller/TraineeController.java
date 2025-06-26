package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.mapper.TraineeMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeDto;
import epam.lab.gymapp.dto.response.register.TraineeRegistrationResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TraineeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainee")
public class TraineeController {
    private final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);
    private final String CONTROLLER = "TraineeController";

    private final TraineeService traineeService;
    private final AuthenticationService authenticationService;

    public TraineeController(TraineeService traineeService, AuthenticationService authenticationService) {
        this.traineeService = traineeService;
        this.authenticationService = authenticationService;

    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Credentials credentials, HttpSession httpSession) {
        LOGGER.debug(CONTROLLER + " executing login process");
        authenticationService.authenticateUser(credentials);
        httpSession.setAttribute("credentials", credentials);
        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<TraineeRegistrationResponse> register(@Valid @RequestBody TraineeRegistrationBody traineeRegistrationBody) {
        LOGGER.debug(CONTROLLER + " executing register process");
        Trainee entity = TraineeMapper.fromDtoToTrainee(traineeRegistrationBody);
        traineeService.createProfile(entity);
        TraineeRegistrationResponse response = new TraineeRegistrationResponse(entity.getUserName(), entity.getPassword());
        return ResponseEntity.ok(response);

    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        traineeService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok("Changed password successfuly");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        Credentials credentials = (Credentials) httpSession.getAttribute("credentials");
        LOGGER.debug("Trainee {} is logging out", credentials.getUsername());
        return ResponseEntity.ok("Trainee logged out");
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainee(@PathVariable("username") String username) {
        Trainee trainee = traineeService.findByUsername(username);
        return ResponseEntity.ok(TraineeMapper.traineeWithTrainers(trainee));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTrainee(@PathVariable("id") Long id, @Valid @RequestBody UpdateTraineeDto updateTraineeDto) {
        Trainee trainee = Trainee.builder()
                .id(id)
                .userName(updateTraineeDto.getUserName())
                .firstName(updateTraineeDto.getFirstName())
                .lastName(updateTraineeDto.getLastName())
                .birthday(updateTraineeDto.getBirthday())
                .address(updateTraineeDto.getAddress())
                .isActive(updateTraineeDto.getIsActive())
                .build();

        Trainee updated = traineeService.updateProfile(trainee);
        return ResponseEntity.ok(TraineeMapper.traineeWithTrainers(updated));

    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteTrainee(@PathVariable("username") String username) {
        traineeService.delete(username);
        return ResponseEntity.ok("Deleted trainee with username  " + username);
    }

    @GetMapping("/getTrainings")
    public ResponseEntity<?> getTraineeTrainings(@RequestParam("userName") String userName,
                                                 @RequestParam(value = "from", required = false) LocalDateTime from,
                                                 @RequestParam(value = "to", required = false) LocalDateTime to,
                                                 @RequestParam(value = "trainerName", required = false) String trainerName,
                                                 @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = traineeService.getTraineeTrainings(userName, from, to, trainerName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainee(t)).toList();
        return ResponseEntity.ok(list);
    }
}
