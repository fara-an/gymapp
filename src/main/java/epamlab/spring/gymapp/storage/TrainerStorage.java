package epamlab.spring.gymapp.storage;

import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Trainer;
@Component("trainerStorage")
public class TrainerStorage extends InMemoryStorage<Trainer> {
}
