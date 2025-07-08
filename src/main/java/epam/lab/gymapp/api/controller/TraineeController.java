package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.error.ErrorResponse;
import epam.lab.gymapp.dto.mapper.TraineeMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeDto;
import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
import epam.lab.gymapp.dto.response.register.TraineeRegistrationResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.service.interfaces.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@Tag(name = "Trainee operations")
public class TraineeController {
    private final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);


    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }


    @Operation(
            summary = "Register a new trainee",
            description = "Creates a user profile and returns the generated credentials."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed (malformed body or policy violations)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials supplied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/")
    public ResponseEntity<TraineeRegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationBody traineeRegistrationBody) {
        LOGGER.debug("Executing register process");
        Trainee entity = TraineeMapper.fromDtoToTrainee(traineeRegistrationBody);
        traineeService.createProfile(entity);
        TraineeRegistrationResponse response = new TraineeRegistrationResponse(entity.getUserName(), entity.getPassword());
        return ResponseEntity.ok(response);

    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession httpSession) {
        Credentials credentials = (Credentials) httpSession.getAttribute("credentials");
        LOGGER.debug("Trainee {} is logging out", credentials.getUsername());
        return ResponseEntity.ok("Trainee logged out");
    }


    @Operation(
            summary = "Fetch trainee profile",
            description = "Looks up a trainee by username and returns their profile along with assigned trainers."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TraineeGetResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainee(
            @PathVariable("username") String username) {
        Trainee trainee = traineeService.findByUsername(username);
        return ResponseEntity.ok(TraineeMapper.traineeWithTrainers(trainee));
    }
    @Operation(
            summary = "Update trainee profile",
            description = "Overwrites selected fields of the trainee identified by ID and returns the updated profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
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
                    description = "Trainee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainee(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateTraineeDto updateTraineeDto) {
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

    @Operation(
            summary = "Delete trainee",
            description = "Removes the trainee identified by username."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee deleted successfully",
                    content = @Content(schema = @Schema (implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteTrainee(
            @PathVariable("username") String username) {
        traineeService.delete(username);
        return ResponseEntity.ok("Deleted trainee with username  " + username);
    }

    @Operation(
            summary = "List trainee trainings",
            description = "Returns all trainings for the given trainee. "
                    + "Filters: date range (`from`, `to`), trainer name, and training type."
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
                    description = "Trainee not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @GetMapping("/{userName}/trainings")
    public ResponseEntity<?> getTraineeTrainings(@PathVariable("userName") String userName,
                                                 @RequestParam(value = "from", required = false) LocalDateTime from,
                                                 @RequestParam(value = "to", required = false) LocalDateTime to,
                                                 @RequestParam(value = "trainerName", required = false) String trainerName,
                                                 @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = traineeService.getTraineeTrainings(userName, from, to, trainerName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainee(t)).toList();
        return ResponseEntity.ok(list);
    }

}
