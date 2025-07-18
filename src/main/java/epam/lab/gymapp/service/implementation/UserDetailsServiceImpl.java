package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.model.UserProfile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    public UserDetailsServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfile userProfile = userDao.findByUsername(username);
        return new User(
                userProfile.getUserName(),
                userProfile.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
