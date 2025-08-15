package epam.lab.gymapp.dto.request.changePassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangeDto {
    @NotBlank
    @Size(min = 6)
    String username;
    String oldPassword;
    String newPassword;

}
