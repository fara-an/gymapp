package epamlab.spring.gymapp.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Training {
    private long id;
    private Trainer trainer;
    private Trainee trainee;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDateTime trainingDate;
    private Duration duration;
    private long trainerId;
    private long traineeId;

    public Training(long id, long traineeId, long trainerId,
                    String trainingName, TrainingType trainingType,
                    LocalDateTime trainingDate, Duration duration) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public LocalDateTime getTrainingDate() {
        return trainingDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public long getTraineeId() {
        return traineeId;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainer=" + trainer.getUserName() +
                ", trainee=" + trainee.getUserName() +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", duration=" + duration +
                ", trainerId=" + trainerId +
                ", traineeId=" + traineeId +
                '}';
    }
}
