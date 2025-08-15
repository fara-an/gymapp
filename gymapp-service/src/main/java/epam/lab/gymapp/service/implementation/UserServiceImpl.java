package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.service.interfaces.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userDao.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public void toggleActiveStatus(String username) {
        userDao.toggleActiveStatus(username);
    }

    @Override
    public UserProfile findByUsername(String username) {
        return userDao.findByUsername(username);
    }


}
