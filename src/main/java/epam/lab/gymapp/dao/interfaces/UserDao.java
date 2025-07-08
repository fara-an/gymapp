package epam.lab.gymapp.dao.interfaces;


public interface UserDao {
    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActiveStatus(String username);
}
