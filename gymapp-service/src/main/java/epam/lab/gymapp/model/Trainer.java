package epam.lab.gymapp.model;

import epam.lab.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name= DatabaseConstants.TABLE_TRAINER)
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainer extends UserProfile {

    @OneToOne
    @JoinColumn(name = DatabaseConstants.COL_SPECIALIZATION)
    private TrainingType specialization;


    @OneToMany(mappedBy = DatabaseConstants.TABLE_TRAINER)
    private List<Training> trainings;


    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "trainers")
    private List<Trainee> trainees;

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", active=" + getIsActive() +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
