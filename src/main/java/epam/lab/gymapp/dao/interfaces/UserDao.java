package epam.lab.gymapp.dao.interfaces;


import epam.lab.gymapp.model.UserProfile;

public interface UserDao {
    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActiveStatus(String username);

    UserProfile findByUsername(String username);


}
