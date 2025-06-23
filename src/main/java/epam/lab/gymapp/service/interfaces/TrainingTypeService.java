package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.model.TrainingType;

public interface TrainingTypeService {
    TrainingType findByName(String name);
}
