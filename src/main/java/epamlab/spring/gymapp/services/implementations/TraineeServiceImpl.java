package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.interfaces.TraineeDao;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.UserProfile;
import epamlab.spring.gymapp.services.interfaces.AuthenticationService;
import epamlab.spring.gymapp.services.interfaces.TraineeService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainee;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private static final String SERVICE_NAME = "TraineeServiceImpl";
    private static final String LOG_DELETE_START = SERVICE_NAME + " - Deleting trainee by username: {}";
    private static final String LOG_DELETE_SUCCESS = SERVICE_NAME + " - Deleted trainee: {}";

    private static final String LOG_QUERY_START = SERVICE_NAME + " - Fetching trainings for trainee {} with [from={}, to={}, trainer={}, type={}]";
    private static final String LOG_QUERY_RESULTS = SERVICE_NAME + " - Retrieved {} trainings for trainee {}";


    private final TraineeDao traineeDao;
    private final AuthenticationService authenticationService;

    public TraineeServiceImpl(TraineeDao traineeDao, AuthenticationService authenticationService) {
        this.traineeDao = traineeDao;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public void delete(Credentials authCredentials, String username) {
        LOGGER.debug(LOG_DELETE_START, username);
        authenticationService.authenticateUser(authCredentials);

        Long id = findByUsername(authCredentials, username).getId();
        traineeDao.delete(id);
        LOGGER.debug(LOG_DELETE_SUCCESS, username);
    }

    @Override
    @Transactional
    public List<Training> getTraineeTrainings(Credentials credentials,String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        LOGGER.debug(LOG_QUERY_START,traineeUsername,fromDate,toDate,trainerName,trainingType);
        authenticationService.authenticateUser(credentials);
        List<Training> traineeTrainings = traineeDao.getTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingType);
        LOGGER.debug(LOG_QUERY_RESULTS, traineeTrainings.size(),traineeUsername);
        return traineeTrainings;

    }

    @Override
    public TraineeDao getDao() {
        return traineeDao;
    }

    @Override
    public AuthenticationService getAuthService() {
        return authenticationService;
    }

    @Override
    public Trainee buildProfile(UserProfile user, Trainee profile) {
        return Trainee.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
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
