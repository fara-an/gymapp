package epam.lab.gymapp.jwt;

import epam.lab.gymapp.model.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "verification_token")
@Getter
@Setter
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "created_timestamp", nullable = false)
    private Timestamp timestamp;


    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;
}
