package epamlab.spring.gymapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.storage.TraineeStorage;
import epamlab.spring.gymapp.storage.TrainerStorage;
import epamlab.spring.gymapp.storage.TrainingStorage;
import epamlab.spring.gymapp.utils.JsonLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "epamlab.spring.gymapp")
@PropertySource("classpath:storage.properties")
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public JsonLoader jsonLoader() {
        return new JsonLoader(objectMapper());
    }

    @Bean
    public TraineeStorage traineeStorage(@Value("${trainee.init.file}") String path) {
        TraineeStorage traineeStorage = new TraineeStorage();
        traineeStorage.setJsonLoader(jsonLoader());
        traineeStorage.setJsonFilePath(path);
        traineeStorage.setItemClass(Trainee.class);
        return traineeStorage;

    }

    @Bean
    public TrainerStorage trainerStorage(@Value("${trainer.init.file}") String path) {
        TrainerStorage trainerStorage = new TrainerStorage();
        trainerStorage.setJsonLoader(jsonLoader());
        trainerStorage.setJsonFilePath(path);
        trainerStorage.setItemClass(Trainer.class);
        return trainerStorage;
    }

    @Bean
    public TrainingStorage trainingStorage(@Value("${training.init.file}") String path) {
        TrainingStorage trainingStorage = new TrainingStorage();
        trainingStorage.setJsonLoader(jsonLoader());
        trainingStorage.setJsonFilePath(path);
        trainingStorage.setItemClass(Training.class);
        return trainingStorage;
    }

}
