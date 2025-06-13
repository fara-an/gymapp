package epamlab.spring.gymapp.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Trainee extends BaseEntity<Long> {
    private LocalDateTime birthday;
    private String address;

    @OneToOne
    @JoinColumn(name ="user_id" ,referencedColumnName = "id")
    private UserEntity<Long> userEntity;

    @ManyToMany(mappedBy = "trainees")
    List<Trainer> trainers;

    @OneToMany(mappedBy = "trainee")
    private List<Training> trainings;

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + getId() +
                ", firstName='" + userEntity.getFirstName() + '\'' +
                ", lastName='" + userEntity.getLastName() + '\'' +
                ", userName='" + userEntity.getUserName() + '\'' +
                ", password='" + userEntity.getPassword() + '\'' +
                ", active=" + userEntity.getIsActive() +
                ", birthday=" + getBirthday() +
                ", address='" + getAddress() + '\'' +
                '}';
    }
}
