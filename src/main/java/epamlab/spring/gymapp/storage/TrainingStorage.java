package epamlab.spring.gymapp.storage;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.TrainingType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Training;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component("trainingStorage")
public class TrainingStorage extends InMemoryStorage<Training> {

    public static final Logger LOGGER = LoggerFactory.getLogger(TrainingStorage.class);
    private Environment env;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @PostConstruct
    private void loadTrainings() {
        String path = env.getProperty("training.init.file");
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");
                Training training = new Training(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]), parts[3], TrainingType.valueOf(parts[4]), LocalDateTime.parse(parts[5]), Duration.ofMinutes(Long.parseLong(parts[6])

                ));
                this.save(training.getId(), training);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }

    }

    @EventListener(ContextRefreshedEvent.class)
    private void linkTrainingReferences() {
        for (Training training : this.getAll()) {
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
