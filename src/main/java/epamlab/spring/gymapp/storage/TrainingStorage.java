package epamlab.spring.gymapp.storage;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import epamlab.spring.gymapp.model.Training;

public class TrainingStorage extends AbstractStorage<Training, Long> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TrainingStorage.class);
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }


    @EventListener(ContextRefreshedEvent.class)
    private void linkTrainingReferences() {
        for (Training training : getAll()) {
            Trainer trainer = trainerStorage.get(training.getTrainerId());
            Trainee trainee = traineeStorage.get(training.getTraineeId());
            if (trainer != null && trainee != null) {
                LOGGER.info("Trainer and trainee is getting set ");
                training.setTrainer(trainer);
                training.setTrainee(trainee);

            }
        }
    }
}

