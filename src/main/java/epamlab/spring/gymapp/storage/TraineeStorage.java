package epamlab.spring.gymapp.storage;

import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Trainee;
@Component("traineeStorage")
public class TraineeStorage extends InMemoryStorage<Trainee> {
}
