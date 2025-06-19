package epam.lab.gymapp.aspect;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.exceptions.UnauthorizedException;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckCredentialsAspect {

    @Autowired
    private AuthenticationService authenticationService;

    @Pointcut("execution(* epam.lab.gymapp.service.implementation)")
    public void appliedMethods() {
    }

    @Before("appliedMethods()")
    public void authenticate() {
        Credentials credentials = CredentialsContextHolder.getCredentials();
        if (credentials == null) {
            throw new UnauthorizedException("Missing Credentials");
        }
        authenticationService.authenticateUser(credentials);
    }
}
