package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.annotation.security.RequiresAuthentication;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.service.interfaces.TraineeService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String SERVICE_NAME = "TraineeServiceImpl";
    private final TraineeDao traineeDao;

    public TraineeServiceImpl(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    @Transactional
    @RequiresAuthentication
    public void delete(String username) {
        LOGGER.debug(SERVICE_NAME + " - Deleting trainee by username: {}", username);
        Long id = findByUsername(username).getId();
        traineeDao.delete(id);
        LOGGER.debug(SERVICE_NAME + " - Deleted trainee: {}", username);
    }

    @RequiresAuthentication
    @Override
    @Transactional
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        LOGGER.debug(SERVICE_NAME + " - Fetching trainings for trainee {} with [from={}, to={}, trainer={}, type={}]", traineeUsername, fromDate, toDate, trainerName, trainingType);
        List<Training> traineeTrainings = traineeDao.getTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingType);
        LOGGER.debug(SERVICE_NAME + " - Retrieved {} trainings for trainee {}", traineeTrainings.size(), traineeUsername);
        return traineeTrainings;

    }

    @Override
    public TraineeDao getDao() {
        return traineeDao;
    }

    @Override
    public Trainee buildProfile(UserProfile user, Trainee profile) {
        return Trainee.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .birthday(profile.getBirthday())
                .address(profile.getAddress())
                .build();
    }

    @Override
    public void updateProfileSpecificFields(Trainee existing, Trainee item) {
        Optional.ofNullable(item.getBirthday()).ifPresent(existing::setBirthday);
        Optional.ofNullable(item.getAddress()).ifPresent(existing::setAddress);
    }


}
