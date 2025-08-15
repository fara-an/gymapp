package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.model.UserProfile;

public interface UserService {

     void changePassword(String username, String oldPassword, String newPassword) ;

     void toggleActiveStatus(String username);

     UserProfile findByUsername(String username);


}
