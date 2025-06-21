package epam.lab.gymapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TraineeRegistrationBody {

    @NotBlank
    @Size(min = 8, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 8, max = 100)
    private String lastName;

    private LocalDateTime dateOfBirth;

    private String address;


}
