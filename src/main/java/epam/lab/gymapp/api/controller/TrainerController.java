package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.error.ErrorResponse;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.changePassword.PasswordChangeDto;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTrainerDto;
import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final AuthenticationService authenticationService;

    public TrainerController(TrainerService trainerService, TrainingTypeService trainingTypeService, AuthenticationService authenticationService) {
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Log in",
            description = "Validates user credentials and stores them in the HTTP session on success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",  content      = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request body(validation failed)", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    @GetMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Credentials credentials, HttpSession session) {
        performLogin(credentials.getUsername(), credentials.getPassword(), session);
        return ResponseEntity.ok("Login successful");

    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerRegistrationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<TrainerRegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationBody registrationDto, HttpSession session) {
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

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                     content      = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed (e.g., blank new password)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Old password is incorrect",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        trainerService.changePassword(passwordChangeDto.getUsername(), passwordChangeDto.getOldPassword(), passwordChangeDto.getNewPassword());
        return ResponseEntity.ok("Changes the password successfully");
    }

    @Operation(
            summary = "Fetch trainer profile",
            description = "Looks up a trainer by username and returns their profile along with assigned trainers."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "trainer found",
                    content      = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerGetResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description= "trainer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainer(
            @PathVariable("username") String username) {
        Trainer trainer = trainerService.findByUsername(username);
        TrainerGetResponse entity = TrainerMapper.dtoWithTraineeList(trainer);
        return ResponseEntity.ok(entity);

    }

    @Operation(
            summary = "Update trainer profile",
            description = "Overwrites selected fields of the trainer identified by ID and returns the updated profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeGetResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/update/{id}")
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

    @Operation(
            summary = "List unassigned trainers",
            description = "Returns all trainers not currently assigned to the given trainee."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List retrieved successfully",
                    content      = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainerWithoutTraineesResponse.class)
                    )

            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error (DAO failure)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/notAssignedToTrainee/{username}")
    public ResponseEntity<?> getTrainersNotAssignedToTrainee(
            @PathVariable("username") String username) {
        List<Trainer> trainers = trainerService.trainersNotAssignedToTrainee(username);
        List<TrainerWithoutTraineesResponse> list = trainers.stream().map(t -> TrainerMapper.dtoWithoutTraineeList(t)).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "List trainer trainings",
            description = "Returns all trainings led by the specified trainer. "
                    + "Optional filters: date range (`from`, `to`), trainer name, training type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainings fetched successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainingResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @GetMapping("/getTrainings")
    public ResponseEntity<?> getTrainerTrainings(
            @RequestParam("userName") String userName,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to,
            @RequestParam(value = "traineeName", required = false) String traineeName,
            @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = trainerService.getTrainerTrainings(userName, from, to, traineeName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainer(t)).toList();
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "Toggle trainer active status",
            description = "Flips the trainer’s <em>isActive</em> flag (activate ⇄ deactivate)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status toggled successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping("/toggleStatus")
    public ResponseEntity<?> toggleStatus(
            @RequestParam String username) {
        trainerService.toggleActiveStatus(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private void performLogin(String username, String password, HttpSession session) {
        Credentials credentials = new Credentials(username, password);
        authenticationService.authenticateUser(credentials);
        session.setAttribute("credentials", credentials);
    }


}
