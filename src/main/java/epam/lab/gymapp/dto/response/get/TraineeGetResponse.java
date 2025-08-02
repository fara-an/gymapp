package epam.lab.gymapp.dto.response.get;

import epam.lab.gymapp.dto.mapper.TraineeMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class TraineeGetResponse {
    private String firstName;
    private String lastName;
    private LocalDateTime birthday;
    private String address;
    private boolean isActive;
    private List<TraineeMapper.TraineeGetsResponseTrainer> trainers;



}
