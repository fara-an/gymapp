package epamlab.spring.gymapp.services.interfaces;

import epamlab.spring.gymapp.dao.interfaces.TraineeDao;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Training;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TraineeService extends ProfileOperations<Trainee, Long, TraineeDao> {
    void delete(Credentials authCredentials, String username);

    List<Training> getTraineeTrainings(Credentials credentials,String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);


}
