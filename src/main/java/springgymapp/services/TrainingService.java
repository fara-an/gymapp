package springgymapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springgymapp.dao.TrainingDao;
import springgymapp.model.Training;
@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }


    public void  create(Training training){
        trainingDao.save(training);
    }

    public void  get(Training training){
        trainingDao.get(training.getId());
    }




}
