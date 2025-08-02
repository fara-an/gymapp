package epam.lab.gymapp.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAddDto {
    @NotBlank
    private String traineeUserName;
    @NotBlank
    private String trainerUserName;
    @NotBlank
    private String trainingName;
    @NotBlank
    private String trainingType;
    @NotNull
    private LocalDateTime trainingDateStart;
    @NotNull
    private Integer duration;


}
