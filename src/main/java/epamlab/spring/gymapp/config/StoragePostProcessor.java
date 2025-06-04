package epamlab.spring.gymapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.storage.TraineeStorage;
import epamlab.spring.gymapp.storage.TrainerStorage;
import epamlab.spring.gymapp.storage.TrainingStorage;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class StoragePostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoragePostProcessor.class);
    private final Environment env;
    private TrainingStorage trainingStorage;
    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;

    public StoragePostProcessor(Environment env) {
        this.env = env;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TrainerStorage trainerStore) {
            this.trainerStorage = trainerStore;
            String path = env.getProperty("trainer.init.file");
            loadTrainers((TrainerStorage) bean, path);
        } else if (bean instanceof TraineeStorage traineeStore) {
            this.traineeStorage = traineeStore;
            String path = env.getProperty("trainee.init.file");
            loadTrainees((TraineeStorage) bean, path);
        } else if (bean instanceof TrainingStorage trainingStore) {
            this.trainingStorage = trainingStore;
            String path = env.getProperty("training.init.file");
            loadTrainings((TrainingStorage) bean, path);
        }

        linkTrainingReferences();


        return bean;


    }

    private void loadTrainers(TrainerStorage trainerStorage, String path) {

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");
                Trainer trainer = new Trainer(parts[0], parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]), parts[5], TrainingType.valueOf(parts[6]), null, Long.parseLong(parts[8])


                );
                trainerStorage.save(trainer.getUserId(), trainer);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }
    }


    private void loadTrainees(TraineeStorage traineeStorage, String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");

                Trainee trainee = new Trainee(parts[0], parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]), LocalDateTime.parse(parts[5]), parts[6], Long.parseLong(parts[7])

                );
                traineeStorage.save(trainee.getUserId(), trainee);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }

    }


    private void loadTrainings(TrainingStorage trainingStorage, String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");
                Training training = new Training(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]), parts[3], TrainingType.valueOf(parts[4]), LocalDateTime.parse(parts[5]), Duration.ofMinutes(Long.parseLong(parts[6])

                ));
                trainingStorage.save(training.getId(), training);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }

    }


    private void linkTrainingReferences() {

        if (trainingStorage == null || trainerStorage == null || traineeStorage == null) {
            LOGGER.info("Returning cause storages are null");
            return;
        }
        for (Training training : trainingStorage.getAll()) {

            Trainer trainer = trainerStorage.get(training.getTrainerId());
            Trainee trainee = traineeStorage.get(training.getTraineeId());
            if (trainer != null && trainee != null) {
                LOGGER.info("Trainer and trainee is getting set ");
                training.setTrainer(trainer);
                training.setTrainee(trainee);

                // Linking back if needed
                if (trainer.getTraining() == null) {
                    trainer.setTraining(training);

                }

                if (trainee.getTraining() == null) {
                    trainee.setTraining(training);
                }
            }
        }
    }
}