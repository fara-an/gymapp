package epam.lab.gymapp.cucumber.integration.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerWorkloadSummaryResponse {

    private String trainerUsername;
    private String firstName;
    private String lastName;
    private boolean status;

    private List<YearSummaryDto> years;

}
