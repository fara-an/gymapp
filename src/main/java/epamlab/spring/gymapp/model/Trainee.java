package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainee extends UserEntity<Long> {

    @Column(name = DatabaseConstants.COL_DATE_OF_BIRTH)
    private LocalDateTime birthday;

    private String address;

    @ManyToMany
    @JoinTable(
            name = DatabaseConstants.TABLE_TRAINER_TRAINEE,
            joinColumns = @JoinColumn(name = DatabaseConstants.COL_TRAINEE_ID),
            inverseJoinColumns = @JoinColumn(name = DatabaseConstants.COL_TRAINER_ID)

    )
    List<Trainer> trainers;

    @OneToMany(mappedBy = DatabaseConstants.TABLE_TRAINEE, cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Training> trainings;

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + getId() +
                ", firstName='" + userProfile.getFirstName() + '\'' +
                ", lastName='" + userProfile.getLastName() + '\'' +
                ", userName='" + userProfile.getUserName() + '\'' +
                ", password='" + userProfile.getPassword() + '\'' +
                ", active=" + userProfile.getIsActive() +
                ", birthday=" + getBirthday() +
                ", address='" + getAddress() + '\'' +
                '}';
    }
}
