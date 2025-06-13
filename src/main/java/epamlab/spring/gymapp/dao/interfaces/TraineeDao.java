package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Training;

import java.time.LocalDateTime;
import java.util.List;

public interface TraineeDao extends
        CreateDao<Trainee, Long>,
        ReadDao<Trainee, Long>,
        ReadDaoByUsername<Trainee, Long>,
        UpdateDao<Trainee, Long>{

    List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);



}
