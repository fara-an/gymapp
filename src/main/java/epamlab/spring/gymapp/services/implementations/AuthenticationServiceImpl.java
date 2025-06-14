package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.interfaces.AuthenticationDao;
import epamlab.spring.gymapp.exceptions.InvalidCredentialsException;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.services.interfaces.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationDao authenticationDao;

    public AuthenticationServiceImpl(AuthenticationDao authenticationDao) {
        this.authenticationDao = authenticationDao;
    }

    @Override
    @Transactional(readOnly = true)
    public void authenticateUser(Credentials credentials) {
        if (!authenticationDao.validateCredentials(credentials)) {
            throw new InvalidCredentialsException(credentials.username());
        }
    }
}