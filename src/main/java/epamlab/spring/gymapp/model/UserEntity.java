package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserEntity<ID> extends BaseEntity<ID>{

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = DatabaseConstants.COL_USER_ID, unique = true)
    protected UserProfile userProfile;
}
