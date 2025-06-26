package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.model.Training;

import java.time.LocalDateTime;

public interface TrainingDao extends CreateReadDao<Training, Long> {

    boolean existsTrainerConflict(Long trainerId,
                                  LocalDateTime newStart,
                                  LocalDateTime newEnd);

    boolean existsTraineeConflict(Long traineeID,
                                  LocalDateTime newStart,
                                  LocalDateTime newEnd);


}
