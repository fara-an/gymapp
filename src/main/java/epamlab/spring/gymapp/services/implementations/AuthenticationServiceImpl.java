package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.interfaces.AuthenticationDao;
import epamlab.spring.gymapp.exceptions.InvalidCredentialsException;
import epamlab.spring.gymapp.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;

    public AuthenticationServiceImpl(AuthenticationDao authenticationDao) {
        this.authenticationDao = authenticationDao;
    }

    @Override
    public void authenticateUser(String username, String password) {
        if (!authenticationDao.validateCredentials(username, password)) {
            throw new InvalidCredentialsException(username);
        }
    }
}