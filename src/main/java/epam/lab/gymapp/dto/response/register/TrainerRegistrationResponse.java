package epam.lab.gymapp.dto.response.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class TrainerRegistrationResponse {
    private String username;
    private String password;
}
