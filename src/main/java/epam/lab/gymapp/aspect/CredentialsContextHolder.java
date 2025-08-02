package epam.lab.gymapp.aspect;

import epam.lab.gymapp.dto.request.login.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialsContextHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsContextHolder.class);
    private static final ThreadLocal<Credentials> context = new ThreadLocal<>();

    public static void setCredentials(Credentials credentials) {
        context.set(credentials);
    }

    public static Credentials getCredentials() {
        return context.get();
    }

    public static void clear(){
        LOGGER.debug("Clearing credentials from ContextHolder");
        context.remove();
    }

}
