package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TrainingDao;
import epamlab.spring.gymapp.services.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Training;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training create(Training training) {
        trainingDao.save(training);
        return training;
    }

    @Override
    public Training get(Training training) {
        return trainingDao.get(training.getId()).get();
    }



}
