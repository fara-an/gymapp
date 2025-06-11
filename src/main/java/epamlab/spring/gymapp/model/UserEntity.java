package epamlab.spring.gymapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class UserEntity<ID> extends BaseEntity<ID>{
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;
}
