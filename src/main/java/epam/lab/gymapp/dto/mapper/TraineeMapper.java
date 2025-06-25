package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.request.registration.TraineeRegistrationBody;
import epam.lab.gymapp.model.Trainee;

import java.time.LocalDateTime;

public class TraineeMapper {

   public static Trainee toEntity(TraineeRegistrationBody registrationBody) {

        String firstName = registrationBody.getFirstName();
        String lastName = registrationBody.getLastName();
        LocalDateTime dateOfBirth = registrationBody.getDateOfBirth();
        String address = registrationBody.getAddress();

       return Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthday(dateOfBirth)
                .address(address)
                .build();

    }
}
