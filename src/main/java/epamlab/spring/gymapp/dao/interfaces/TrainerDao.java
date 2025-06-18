package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TrainerDao extends
        CrudDao<Trainer,Long> {

    List<Training> getTrainerTrainings(Credentials credentials, String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername );
    List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);


}
