package epamlab.spring.gymapp.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Training extends BaseEntity<Long>{
    private Trainer trainer;
    private Trainee trainee;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDateTime trainingDate;
    private Duration duration;
    private long trainerId;
    private long traineeId;

    @Override
    public String toString() {
        return "Training{" +
                "id=" + getId() +
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
