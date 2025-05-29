package springgymapp.dao;

import org.springframework.stereotype.Component;
import springgymapp.model.Trainer;
@Component("trainerStorage")
public class TrainerStorage extends InMemoryStorage<Trainer> {
}
