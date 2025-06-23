package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.dto.registration.TraineeRegistrationBody;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TraineeService extends ProfileOperations<Trainee, TraineeDao, TraineeRegistrationBody> {
    void delete(Credentials authCredentials, String username);

    List<Training> getTraineeTrainings(Credentials credentials, String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);


}
