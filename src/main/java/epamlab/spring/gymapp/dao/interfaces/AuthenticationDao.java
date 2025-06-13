package epamlab.spring.gymapp.dao.interfaces;

import org.springframework.stereotype.Component;

@Component
public interface AuthenticationDao {
    boolean validateCredentials(String username, String password);
}
