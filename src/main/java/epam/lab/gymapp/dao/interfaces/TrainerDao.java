package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface TrainerDao extends
        CrudDao<Trainer,Long> {

    List<Training> getTrainerTrainings(Credentials credentials, String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername );
    List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);


}
