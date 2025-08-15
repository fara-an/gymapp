package epam.lab.gymapp.model;

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
