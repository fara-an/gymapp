package springgymapp.dao;

import org.springframework.stereotype.Component;
import springgymapp.model.Training;
@Component("trainingStorage")
public class TrainingStorage extends InMemoryStorage<Training> {
}
