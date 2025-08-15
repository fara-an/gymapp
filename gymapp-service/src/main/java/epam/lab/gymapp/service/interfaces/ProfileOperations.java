package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dao.interfaces.CreateReadUpdateDao;
import epam.lab.gymapp.model.UserProfile;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileOperations<T extends UserProfile, D extends CreateReadUpdateDao<T, Long>> {
    D getDao();

    T buildProfile(UserProfile user, T profile);

    void updateProfileSpecificFields(T existing, T item);

    @Transactional
    T createProfile(T item);

    @Transactional
    T updateProfile(T item);

    @Transactional(readOnly = true)
    T findByUsername(String username);

    @Transactional(readOnly = true)
    T findById(Long id);
}
