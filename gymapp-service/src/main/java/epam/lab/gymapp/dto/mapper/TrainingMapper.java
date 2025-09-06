package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.response.training.TrainingResponse;
import epam.lab.gymapp.model.Training;

public class TrainingMapper {

    public static TrainingResponse trainingWithTrainee(Training training) {
        TrainingResponse trainingResponse = trainingResponse(training);
        trainingResponse.setTraineeName(training.getTrainee().getUserName());
        return trainingResponse;
    }

    public static TrainingResponse trainingWithTrainer(Training training) {
        TrainingResponse trainingResponse = trainingResponse(training);
        trainingResponse.setTrainerName(training.getTrainer().getUserName());
        return trainingResponse;
    }

    private static TrainingResponse trainingResponse(Training training) {
        return TrainingResponse.builder()
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType().getName())
                .trainingDateStart(training.getTrainingDateStart())
                .traineeName(training.getTrainee().getUserName())
                .trainerName(training.getTrainer().getUserName())
                .build();

    }

}
