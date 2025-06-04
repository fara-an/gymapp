package epamlab.spring.gymapp.config;

import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.storage.TraineeStorage;
import epamlab.spring.gymapp.storage.TrainerStorage;
import epamlab.spring.gymapp.storage.TrainingStorage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import epamlab.spring.gymapp.model.Trainee;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        TraineeStorage traineeStorage = annotationConfigApplicationContext.getBean(TraineeStorage.class);
        TrainerStorage trainerStorage = annotationConfigApplicationContext.getBean(TrainerStorage.class);
        TrainingStorage trainingStorage = annotationConfigApplicationContext.getBean(TrainingStorage.class);


        List<Trainee> trainees = traineeStorage.getAll();
        trainees.forEach(System.out::println);

        List<Trainer> trainers = trainerStorage.getAll();
        trainers.forEach(System.out::println);

        List<Training> trainings = trainingStorage.getAll();
        trainings.forEach(System.out::println);


    }
}
