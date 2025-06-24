package epam.lab.gymapp.aspect;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.exceptions.UnauthorizedException;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckCredentialsAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuthenticationService authenticationService;

    @Pointcut("@annotation( epam.lab.gymapp.annotation.security.RequiresAuthentication)")
    public void appliedMethods() {
    }

    @Before("appliedMethods()")
    public void authenticate() {
        LOGGER.debug("Aspect checking started.");
        Credentials credentials = CredentialsContextHolder.getCredentials();
        if (credentials == null) {
            throw new UnauthorizedException("Missing Credentials");
        }
        authenticationService.authenticateUser(credentials);
        LOGGER.debug("Aspect checking ended.");
    }
}
