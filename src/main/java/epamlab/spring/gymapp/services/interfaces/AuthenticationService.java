package epamlab.spring.gymapp.services.interfaces;


import epamlab.spring.gymapp.dto.Credentials;

public interface AuthenticationService  {
     void authenticateUser(Credentials credentials);
}
