package epamlab.spring.gymapp.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "training_type")
public class TrainingType extends BaseEntity<Long>{

    private String name;
}
