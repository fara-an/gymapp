package epamlab.spring.gymapp.storage;

import epamlab.spring.gymapp.model.TrainingType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import epamlab.spring.gymapp.model.Trainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component("trainerStorage")
public class TrainerStorage extends InMemoryStorage<Trainer> {

    Environment env;

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @PostConstruct
    private void loadTrainers() {
        String path = env.getProperty("trainer.init.file");

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.lines().forEach(line -> {
                String[] parts = line.split(",");
                Trainer trainer = new Trainer(parts[0], parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4]), parts[5], TrainingType.valueOf(parts[6]), Long.parseLong(parts[8])


                );
                this.save(trainer.getUserId(), trainer);
            });
        } catch (IOException io) {
            throw new RuntimeException("Failed to load trainers from " + path, io);
        }
    }
}
