package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.dto.request.login.Credentials;

public interface AuthenticationService  {
     void authenticateUser(Credentials credentials);
}
