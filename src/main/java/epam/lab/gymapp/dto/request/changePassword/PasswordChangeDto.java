package epam.lab.gymapp.dto.request.changePassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeDto {
    @NotBlank
    @Size(min = 6)
    String username;
    String oldPassword;
    String newPassword;

}
