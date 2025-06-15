package epamlab.spring.gymapp.model;

import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;



@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private ID id;
}
