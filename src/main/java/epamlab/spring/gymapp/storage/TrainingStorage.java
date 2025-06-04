package epamlab.spring.gymapp.storage;

import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Training;
@Component("trainingStorage")
public class TrainingStorage extends InMemoryStorage<Training> {
}
