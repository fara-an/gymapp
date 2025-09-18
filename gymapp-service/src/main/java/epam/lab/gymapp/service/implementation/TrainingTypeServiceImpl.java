package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.TrainingTypeDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeServiceImpl.class);

    TrainingTypeDao<TrainingType, Long> dao;

    public TrainingTypeServiceImpl(TrainingTypeDao<TrainingType, Long> dao) {
        this.dao = dao;
    }

    @Transactional
    @Override
    public TrainingType findByName(String name) {
        String serviceName = getClass().getSimpleName();
        TrainingType trainingType = dao.findByName(name).orElseThrow(() -> {
            String msg = String.format("%s: Entity with name '%s' not found.", serviceName, name);
            LOGGER.error("{}: SERVICE ERROR - Entity with name '{}' not found", serviceName, name);
            return new EntityNotFoundException(msg);
        });
        LOGGER.debug("{}: SERVICE - Entity found by name: {}", serviceName, name);
        return trainingType;

    }
}
