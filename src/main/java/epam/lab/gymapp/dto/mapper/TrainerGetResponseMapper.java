package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.response.get.TrainerGetResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class TrainerGetResponseMapper {
    public static TrainerGetResponse dtoWithTraineeList(Trainer trainer) {
        List<TraineeDto> responseTrainers = mapListOfTrainees(trainer.getTrainees());
        return TrainerGetResponse.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .isActive(trainer.getIsActive())
                .specialization(trainer.getSpecialization())
                .trainees(responseTrainers)
                .build();

    }

    private static List<TraineeDto> mapListOfTrainees(List<Trainee> trainees) {
        return trainees.stream().map(t -> mapTrainer(t)).toList();
    }

    private static TraineeDto mapTrainer(Trainee trainee) {
        return TraineeDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .userName(trainee.getUserName())
                .build();
    }

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TraineeDto {
        String userName;
        String firstName;
        String lastName;

    }

}
