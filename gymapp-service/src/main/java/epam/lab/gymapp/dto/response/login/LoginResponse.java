package epam.lab.gymapp.dto.response.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginResponse {
    private String message;
    private String token;
}
