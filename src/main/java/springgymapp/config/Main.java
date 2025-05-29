package springgymapp.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springgymapp.dao.TraineeStorage;
import springgymapp.dao.TrainerStorage;
import springgymapp.dao.TrainingStorage;
import springgymapp.model.Trainee;
import springgymapp.model.Trainer;
import springgymapp.model.Training;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        TraineeStorage  traineeStorage = annotationConfigApplicationContext.getBean(TraineeStorage.class);
        TrainerStorage trainerStorage = annotationConfigApplicationContext.getBean(TrainerStorage.class);
        TrainingStorage trainingStorage = annotationConfigApplicationContext.getBean(TrainingStorage.class);


        List<Trainee> trainees = traineeStorage.getAll();
        trainees.forEach(System.out::println);

        List<Trainer> trainers = trainerStorage.getAll();
        trainers.forEach(System.out::println);

        List<Training> trainings =  trainingStorage.getAll();
        trainings.forEach(System.out::println);


    }
}
