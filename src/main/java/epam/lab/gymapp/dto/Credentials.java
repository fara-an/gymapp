package epam.lab.gymapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    @NotNull
    @NotBlank
    private String username;
    private String password;

}
