package springgymapp.services;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springgymapp.dao.TraineeDao;
import springgymapp.model.Trainee;
import org.slf4j.Logger;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
@Service
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final Random random = new SecureRandom();


    private TraineeDao traineeDao;

    @Autowired
    public void setTrainerDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }


    public Trainee createTrainee(String firstName, String lastName, boolean isActive, LocalDateTime birthday, String address, long userId) {
        logger.info("Creating trainer profile {} {}", firstName, lastName);
        String username = generateUsername(firstName, lastName);
        String password = generatePassword();
        Trainee trainee = new Trainee(firstName, lastName, username, password, isActive, birthday, address, userId);
        traineeDao.save(trainee);
        return trainee;
    }

    public Trainee getTrainee(String userName) {
        Optional<Trainee> optionalTrainee = traineeDao.get(userName);
        return optionalTrainee.orElse(null);
    }

    public void updateTrainee(long id, Trainee updatedTrainee){
        Optional<Trainee> trainee = traineeDao.get(id);
        if (trainee.isPresent()){
            Trainee t= new Trainee(updatedTrainee.getFirstName(), updatedTrainee.getLastName(),
                    updatedTrainee.getUserName(), updatedTrainee.getPassword(),
                    updatedTrainee.isActive(), updatedTrainee.getBirthday(), updatedTrainee.getAddress(), id);
            traineeDao.update(id,t);
            return;
        }
        logger.warn("Trainer with id {} not found", updatedTrainee.getUserId());

    }

    public void deleteTrainee(Trainee trainee){
        traineeDao.delete(trainee);
    }




    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String userName = baseUsername;
        int suffix = 1;
        while (traineeDao.get(userName).isPresent()) {
            userName = baseUsername + suffix;
            suffix++;
        }
        return userName;
    }


    private String generatePassword() {
        StringBuilder password = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }


}
