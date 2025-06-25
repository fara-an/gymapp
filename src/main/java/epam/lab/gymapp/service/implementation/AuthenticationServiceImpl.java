package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.AuthenticationDao;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
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
            throw new InvalidCredentialsException(credentials.getUsername());
        }
    }
}