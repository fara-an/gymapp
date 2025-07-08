package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.service.interfaces.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService<UserProfile, CreateReadDao<UserProfile, Long>> {

    CreateReadDao<UserProfile, Long> createReadDao;

    @Override
    public CreateReadDao<UserProfile, Long> getDao() {
      return   createReadDao;
    }
}
