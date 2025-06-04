package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.dao.TraineeDao;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainee;
import org.slf4j.Logger;
import epamlab.spring.gymapp.services.serviceInterfaces.TraineeService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;

    private DotSeparatedUsernameAndPasswordGeneration usernameAndPasswordGenerator;

    public TraineeServiceImpl(TraineeDao traineeDao, DotSeparatedUsernameAndPasswordGeneration usernameAndPasswordGenerator) {
        this.traineeDao = traineeDao;
        this.usernameAndPasswordGenerator = usernameAndPasswordGenerator;
    }

    @Autowired
    public void setTrainerDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }


    @Override
    public Trainee createTrainee(String firstName, String lastName, boolean isActive, LocalDateTime birthday, String address, long userId) {
        LOGGER.info("Creating trainer profile {} {}", firstName, lastName);
        String userName = firstName + "." + lastName;
        long numberOfSameUsernames = traineeDao.findUsernamesStartsWith(userName);
        String username = usernameAndPasswordGenerator.generateUsername(firstName, lastName, numberOfSameUsernames);
        String password = usernameAndPasswordGenerator.generatePassword();
        Trainee trainee = new Trainee(firstName, lastName, username, password, isActive, birthday, address, userId);
        traineeDao.save(trainee);
        return trainee;
    }

    @Override
    public Trainee getTrainee(String userName) {
        Optional<Trainee> optionalTrainee = traineeDao.get(userName);
        return optionalTrainee.orElse(null);
    }

    @Override
    public void updateTrainee(long id, Trainee updatedTrainee) {
        Optional<Trainee> trainee = traineeDao.get(id);
        if (trainee.isPresent()) {
            Trainee t = new Trainee(updatedTrainee.getFirstName(), updatedTrainee.getLastName(),
                    updatedTrainee.getUserName(), updatedTrainee.getPassword(),
                    updatedTrainee.isActive(), updatedTrainee.getBirthday(), updatedTrainee.getAddress(), id);
            traineeDao.update(id, t);
            return;
        }
        LOGGER.warn("Trainer with id {} not found", updatedTrainee.getUserId());

    }

    public void deleteTrainee(long id) {
        traineeDao.delete(id);
    }


}
