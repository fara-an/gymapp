package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "training")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Training extends BaseEntity<Long> {
    @ManyToOne
    @JoinColumn(name = DatabaseConstants.COL_TRAINER_ID, referencedColumnName = DatabaseConstants.COL_USER_ID, nullable = false)
    private Trainer trainer;

    @ManyToOne
    @JoinColumn(name = DatabaseConstants.COL_TRAINEE_ID, referencedColumnName = DatabaseConstants.COL_USER_ID, nullable = false)
    private Trainee trainee;

    @Column(name = DatabaseConstants.COL_TRAINING_NAME,  nullable = false)
    private String trainingName;

    @ManyToOne
    @JoinColumn(name = DatabaseConstants.COL_TRAINING_TYPE_ID,  nullable = false)
    private TrainingType trainingType;

    @Column(name = DatabaseConstants.COL_TRAINING_DATE, nullable = false)
    private LocalDateTime trainingDate;
    @Column(name = DatabaseConstants.COL_TRAINING_DURATION, nullable = false)
    private Double duration;


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
                '}';
    }
}
