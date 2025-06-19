package epam.lab.gymapp.model;

import epam.lab.gymapp.jwt.VerificationToken;
import epam.lab.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "userprofile")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = DatabaseConstants.COL_USER_ID))
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

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @OneToMany(mappedBy = "userProfile",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc ")
    private List<VerificationToken> verificationTokens = new ArrayList<>();


}
