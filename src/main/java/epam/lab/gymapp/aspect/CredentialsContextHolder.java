package epam.lab.gymapp.aspect;

import epam.lab.gymapp.dto.request.login.Credentials;

public class CredentialsContextHolder {

    private static final ThreadLocal<Credentials> context = new ThreadLocal<>();

    public static void setCredentials(Credentials credentials) {
        context.set(credentials);
    }

    public static Credentials getCredentials() {
        return context.get();
    }

    public static void clear(){
        context.remove();;
    }

}
