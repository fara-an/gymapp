package epam.lab.gymapp.service.interfaces;





public interface UserService {

     void changePassword(String username, String oldPassword, String newPassword) ;

     void toggleActiveStatus(String username) ;


}
