package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.error.ErrorResponse;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
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
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @PostMapping("/")
    public ResponseEntity<TrainerRegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationBody registrationDto, HttpSession session) {
        Trainer trainer = Trainer.builder().
                firstName(registrationDto.getFirstName()).
                lastName(registrationDto.getLastName()).
                build();
        TrainingType specialization = trainingTypeService.findByName(registrationDto.getTrainingType());
        trainer.setSpecialization(specialization);
        Trainer newTrainer = trainerService.createProfile(trainer);
        return ResponseEntity.ok(TrainerMapper.dtoOnlyUsernameAndPass(newTrainer));
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
    @GetMapping("/{username}/unassigned-trainers")
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

    @GetMapping("/{username}/trainings")
    public ResponseEntity<?> getTrainerTrainings(
            @PathVariable("userName") String userName,
            @RequestParam(value = "from", required = false) LocalDateTime from,
            @RequestParam(value = "to", required = false) LocalDateTime to,
            @RequestParam(value = "traineeName", required = false) String traineeName,
            @RequestParam(value = "trainingType", required = false) String trainingType) {
        List<Training> trainings = trainerService.getTrainerTrainings(userName, from, to, traineeName, trainingType);
        List<TrainingResponse> list = trainings.stream().map(t -> TrainingMapper.trainingWithTrainer(t)).toList();
        return ResponseEntity.ok(list);
    }







}
