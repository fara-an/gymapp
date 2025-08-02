package epam.lab.gymapp.dto.request.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerRegistrationBody implements RegistrationDto{

    @NotBlank
    @Size(min = 8, max = 100)
    private String firstName;

    @NotBlank
    @Size(min = 8, max = 100)
    private String lastName;
    @NotBlank
    private String trainingType;
}
