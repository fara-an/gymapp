package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.services.CreateReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Training;

import java.util.Optional;

@Service
public class TrainingServiceImpl implements CreateReadService<Training, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training create(Training training) {
        LOGGER.info("Creating training: name='{}', type='{}'", training.getTrainingName(), training.getTrainingType());
        trainingDao.create(training);
        LOGGER.info("Training created successfully with ID: {}", training.getId());
        return training;
    }

    @Override
    public Training findById(Long id) {
        LOGGER.info("Retrieving training with ID: {}", id);
        Optional<Training> foundTraining = trainingDao.findById(id);

        if (foundTraining.isPresent()) {
            LOGGER.info("Training with ID {} retrieved successfully: name='{}'",
                    id, foundTraining.get().getTrainingName());
            return foundTraining.get();
        } else {
            LOGGER.warn("Training with ID {} not found.", id);
            return null;
        }
    }



}
