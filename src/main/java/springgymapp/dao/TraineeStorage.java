package springgymapp.dao;

import org.springframework.stereotype.Component;
import springgymapp.model.Trainee;
@Component("traineeStorage")
public class TraineeStorage extends InMemoryStorage<Trainee> {
}
