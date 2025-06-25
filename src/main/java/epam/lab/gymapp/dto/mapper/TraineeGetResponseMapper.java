package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.response.get.TraineeGetResponse;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.TrainingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class TraineeGetResponseMapper {
    public static TraineeGetResponse toEntity(Trainee trainee) {
        List<TraineeGetsResponseTrainer> responseTrainers = mapListOfTrainers(trainee.getTrainers());
        return TraineeGetResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .birthday(trainee.getBirthday())
                .address(trainee.getAddress())
                .isActive(trainee.getIsActive())
                .trainers(responseTrainers)
                .build();

    }

    private static List<TraineeGetsResponseTrainer> mapListOfTrainers(List<Trainer> trainers) {
        return trainers.stream().map(t -> mapTrainer(t)).toList();
    }

    private static TraineeGetsResponseTrainer mapTrainer(Trainer trainer) {
        return TraineeGetsResponseTrainer.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(trainer.getUserName())
                .specialization(trainer.getSpecialization())
                .build();
    }

    @SuperBuilder
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TraineeGetsResponseTrainer {
        String userName;
        String firstName;
        String lastName;
        TrainingType specialization;

    }

}
