package epamlab.spring.gymapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainer extends BaseEntity<Long> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType specialization;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity<Long> userEntity;

    @OneToMany(mappedBy = "trainer")
    private List<Training> trainings;

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees;

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + userEntity.getFirstName() + '\'' +
                ", lastName='" + userEntity.getLastName() + '\'' +
                ", userName='" + userEntity.getUserName() + '\'' +
                ", password='" + userEntity.getPassword() + '\'' +
                ", active=" + userEntity.getIsActive() +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
