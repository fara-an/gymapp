package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dao.interfaces.CreateReadUpdateDao;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.dto.request.update.UpdateTraineeTrainerList;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.UserProfile;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TraineeService extends ProfileOperations<Trainee, TraineeDao> {

    void delete( String username);

    List<Training> getTraineeTrainings(String traineeUsername, @Nullable LocalDateTime fromDate, @Nullable LocalDateTime toDate,  @Nullable String trainerName, @Nullable String trainingType);

    List<Trainer> updateTrainer(String traineeUsername, List<UpdateTraineeTrainerList> list);


}
