package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.dto.Credentials;

public interface AuthenticationDao {
    boolean validateCredentials(Credentials credentials);
}
