package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.service.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userDao.changePassword(username,oldPassword, newPassword);
    }

    @Override
    public void toggleActiveStatus(String username) {
     userDao.toggleActiveStatus(username);
    }
}
