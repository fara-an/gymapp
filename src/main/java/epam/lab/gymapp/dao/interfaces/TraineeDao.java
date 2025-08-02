package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TraineeDao extends CrudDao<Trainee,Long> {
    List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
}
