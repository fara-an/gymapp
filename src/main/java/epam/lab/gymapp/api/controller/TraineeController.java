package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.mapper.TraineeGetResponseMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.mapper.TraineeMapper;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeDto;
import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
import epam.lab.gymapp.dto.response.register.TraineeRegistrationResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TraineeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainee")
public class TraineeController {
    private final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);
    private final String CONTROLLER = "TraineeController";

    private TraineeService traineeService;
    private AuthenticationService authenticationService;

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
        Trainee entity = TraineeMapper.toEntity(traineeRegistrationBody);
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
        TraineeGetResponse traineeResponse = TraineeGetResponseMapper.toEntity(trainee);
        return ResponseEntity.ok(traineeResponse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTrainee(@PathVariable ("id")Long id, @Valid @RequestBody UpdateTraineeDto updateTraineeDto) {
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
        TraineeGetResponse entity = TraineeGetResponseMapper.toEntity(updated);
        return ResponseEntity.ok(entity);

    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteTrainee(@PathVariable("username") String username){
        traineeService.delete(username);
        return ResponseEntity.ok("Deleted trainee with username  "+username);

    }
}
