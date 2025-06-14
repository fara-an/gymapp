package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "userprofile")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity<Long> {

    @Column(name = DatabaseConstants.COL_FIRST_NAME, nullable = false)
    private String firstName;

    @Column(name = DatabaseConstants.COL_LAST_NAME, nullable = false)
    private String lastName;

    @Column(name = DatabaseConstants.COL_USER_NAME, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(name = DatabaseConstants.COL_IS_ACTIVE, nullable = false)
    private Boolean isActive;
}
