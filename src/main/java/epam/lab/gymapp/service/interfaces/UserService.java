package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.model.UserProfile;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

     void changePassword(String username, String oldPassword, String newPassword) ;

     void toggleActiveStatus(String username);

     UserProfile findByUsername(String username);


}
