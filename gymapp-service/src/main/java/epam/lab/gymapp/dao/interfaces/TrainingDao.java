package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.model.Training;

import java.time.LocalDateTime;

public interface TrainingDao extends CreateReadUpdateDao<Training, Long> {

    boolean existsTrainerConflict(Long trainerId,
                                  LocalDateTime newStart,
                                  LocalDateTime newEnd);

    boolean existsTraineeConflict(Long traineeID,
                                  LocalDateTime newStart,
                                  LocalDateTime newEnd);

    void deleteTraining(Training training);
    Training findTraining(String trainerUsername, String traineeUsername, LocalDateTime start);


}
