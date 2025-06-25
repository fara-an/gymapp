package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TraineeService extends ProfileOperations<Trainee, TraineeDao, TraineeRegistrationBody> {
    void delete( String username);

    List<Training> getTraineeTrainings( String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);


}
