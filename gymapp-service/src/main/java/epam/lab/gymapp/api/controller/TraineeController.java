package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.MessageResponse;
import epam.lab.gymapp.dto.mapper.TraineeMapper;
import epam.lab.gymapp.dto.mapper.TrainerMapper;
import epam.lab.gymapp.dto.mapper.TrainingMapper;
import epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.dto.request.update.UpdateTraineeDto;
import epam.lab.gymapp.dto.response.get.TrainerWithoutTraineesResponse;
import epam.lab.gymapp.dto.response.register.TraineeRegistrationResponse;
import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.service.interfaces.TraineeService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
public class TraineeController {
    private final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;
    private final MeterRegistry meterRegistry;


    @PostMapping
    public ResponseEntity<TraineeRegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationBody traineeRegistrationBody) {
        LOGGER.debug("Executing register process");
        Trainee entity = TraineeMapper.fromDtoToTrainee(traineeRegistrationBody);
        traineeService.createProfile(entity);
        TraineeRegistrationResponse response = new TraineeRegistrationResponse(entity.getUserName(), entity.getPassword());
        return ResponseEntity.ok(response);

    }


    @GetMapping("/{username}")
    public ResponseEntity<?> getTrainee(
            @PathVariable("username") String username) {
        Trainee trainee = traineeService.findByUsername(username);
        return ResponseEntity.ok(TraineeMapper.traineeWithTrainers(trainee));
    }


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


    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteTrainee(
            @PathVariable("username") String username) {
        traineeService.delete(username);
        Counter counter = Counter.builder("api_endpoint_delete_trainee_username_counter")
                .tag("traineeUsername", username)
                .description("Number of requests for trainees delete(String username)")
                .register(meterRegistry);
        counter.increment();

        return ResponseEntity.ok(new MessageResponse("Deleted trainee with username  " + username));
    }

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

    @PatchMapping("/{userName}/trainers")
    public ResponseEntity<?> assignTrainers(
            @PathVariable("userName") String userName,
            @RequestBody List<UpdateTraineeTrainerList> assignments) {

        List<Trainer> trainers = traineeService.updateTrainer(userName, assignments);
        List<TrainerWithoutTraineesResponse> list = trainers.stream().map(t -> TrainerMapper.dtoWithoutTraineeList(t)).toList();
        return ResponseEntity.ok().body(list);
    }


}
