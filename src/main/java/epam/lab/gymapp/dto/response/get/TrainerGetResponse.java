package epam.lab.gymapp.dto.response.get;

import epam.lab.gymapp.dto.mapper.TrainerGetResponseMapper;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.TrainingType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class TrainerGetResponse {
    private String firstName;
    private String lastName;
    private TrainingType specialization;
    private boolean isActive;
    private List<TrainerGetResponseMapper.TraineeDto> trainees;


}
