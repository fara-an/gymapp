package epam.lab.gymapp.dto.response.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeRegistrationResponse {
   private String username;
   private String password;
}
