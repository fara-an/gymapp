package epamlab.spring.gymapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Training extends BaseEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;
    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private Trainee trainee;
    @NotBlank
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;
    @NotNull
    private LocalDateTime trainingDate;
    @NotNull
    private Double duration;
    private long trainerId;
    private long traineeId;

    @Override
    public String toString() {
        return "Training{" +
                "id=" + getId() +
                ", trainer=" + trainer.getUserEntity().getUserName() +
                ", trainee=" + trainee.getUserEntity().getUserName() +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", duration=" + duration +
                ", trainerId=" + trainerId +
                ", traineeId=" + traineeId +
                '}';
    }
}
