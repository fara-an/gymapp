package epam.lab.gymapp.dto.response.register;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
public class TrainerRegistrationResponse {
    private String username;
    private String password;
}
