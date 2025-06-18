package epamlab.spring.gymapp.config;


import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        TrainingManagementFacade facade = annotationConfigApplicationContext.getBean(TrainingManagementFacade.class);
        Credentials credentials = new Credentials("Emily.Brown", "pass789");
        LocalDateTime fromDate = LocalDateTime.parse("2025-06-14T08:00:00");
        double hours = 1.5;
        LocalDateTime toDate = fromDate.plusMinutes((long) (hours * 60));

        List<Training> trainings = facade.getTraineeTrainingsBasedOn(credentials, credentials.getUsername(), fromDate, toDate, "John.Doe", "Strength Training");
        trainings.forEach(t -> System.out.println(t.getTrainingName()));
    }
}
