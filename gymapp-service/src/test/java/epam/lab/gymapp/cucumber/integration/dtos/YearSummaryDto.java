package epam.lab.gymapp.cucumber.integration.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YearSummaryDto {
    private int year;
    private List<MonthSummaryDto> months;

}
