package epam.lab.gymapp.dto.response.training;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TrainingResponse {
    private String trainingName;
    private LocalDateTime trainingDateStart;
    private String trainingType;
    private String trainerName;
    private String traineeName;




}
