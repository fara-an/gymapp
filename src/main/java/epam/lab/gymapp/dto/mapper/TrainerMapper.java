package epam.lab.gymapp.dto.mapper;

import epam.lab.gymapp.dto.registration.TrainerRegistrationBody;
import epam.lab.gymapp.model.Trainer;

public class TrainerMapper {

    public static Trainer toEntity(TrainerRegistrationBody trainerRegistrationBody) {

        return Trainer.builder()
                .firstName(trainerRegistrationBody.getFirstName())
                .lastName(trainerRegistrationBody.getLastName())
                .build();
    }
}
