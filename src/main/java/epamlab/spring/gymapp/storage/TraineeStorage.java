package epamlab.spring.gymapp.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Trainee;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

@Component("traineeStorage")
public class TraineeStorage extends InMemoryStorage<Trainee> {

    Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    private void loadTraineesFrom( ) {
        String path = environment.getProperty("trainee.init.file");

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");

                Trainee trainee = new Trainee(parts[0], parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]), LocalDateTime.parse(parts[5]), parts[6], Long.parseLong(parts[7])

                );
                this.save(trainee.getUserId(), trainee);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }

    }

}
