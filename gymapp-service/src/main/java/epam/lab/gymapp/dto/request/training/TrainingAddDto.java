package epam.lab.gymapp.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private Integer duration;


}
