package epamlab.spring.gymapp.services.serviceInterfaces;

import epamlab.spring.gymapp.model.Trainee;

import java.time.LocalDateTime;

public interface TraineeService {
    Trainee createTrainee(String firstName, String lastName, boolean isActive, LocalDateTime birthday, String address, long userId);

    Trainee getTrainee(String userName);

    void updateTrainee(long id, Trainee updatedTrainee);

    void deleteTrainee(long id);
}
