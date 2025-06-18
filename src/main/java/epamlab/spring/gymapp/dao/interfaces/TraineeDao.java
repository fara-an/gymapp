package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Training;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TraineeDao extends CrudDao<Trainee,Long> {
    List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
}
