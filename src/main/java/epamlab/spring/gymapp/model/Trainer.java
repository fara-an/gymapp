package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainer extends UserEntity<Long> {

    @OneToOne
    @JoinColumn(name = DatabaseConstants.COL_SPECIALIZATION)
    private TrainingType specialization;


    @OneToMany(mappedBy = DatabaseConstants.TABLE_TRAINER)
    private List<Training> trainings;


    @ManyToMany(mappedBy = "trainers")
    private List<Trainee> trainees;

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + userProfile.getFirstName() + '\'' +
                ", lastName='" + userProfile.getLastName() + '\'' +
                ", userName='" + userProfile.getUserName() + '\'' +
                ", password='" + userProfile.getPassword() + '\'' +
                ", active=" + userProfile.getIsActive() +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
