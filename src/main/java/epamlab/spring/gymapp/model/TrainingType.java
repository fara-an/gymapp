package epamlab.spring.gymapp.model;


import epamlab.spring.gymapp.utils.DatabaseConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = DatabaseConstants.TABLE_TRAINING_TYPE)
@Getter
@Setter
public class TrainingType extends BaseEntity<Long>{
    @Column(name=DatabaseConstants.COL_TRAINING_TYPE_NAME)
    private String name;
}
