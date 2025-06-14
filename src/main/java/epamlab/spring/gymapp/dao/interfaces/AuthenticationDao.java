package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.dto.Credentials;

public interface AuthenticationDao {
    boolean validateCredentials(Credentials credentials);
}
