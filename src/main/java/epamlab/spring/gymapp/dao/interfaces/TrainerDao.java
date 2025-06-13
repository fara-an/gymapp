package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainerDao extends
        CreateDao<Trainer, Long>,
        ReadDao<Trainer, Long>,
        ReadDaoByUsername<Trainer, Long>,
        UpdateDao<Trainer, Long>,
        DeleteDao<Trainer, Long> {

    List<Training> getTrainerTrainings(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername );
    List<Trainer> trainersWithoutTrainees(String traineeUsername);


}
