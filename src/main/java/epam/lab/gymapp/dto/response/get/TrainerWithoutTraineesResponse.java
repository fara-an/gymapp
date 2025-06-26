package epam.lab.gymapp.dto.response.get;

import epam.lab.gymapp.model.TrainingType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class TrainerWithoutTraineesResponse {
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private boolean isActive;
}
