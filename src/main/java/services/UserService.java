
package services;

import javax.ejb.Local;
import javax.ejb.Stateless;

import dao.UserDao;
import models.User;

import java.util.List;

@Local(IUserService.class)
@Stateless

public class UserService implements IUserService {

  @Override
  public User doLogin(String email, String password) {
    UserDao userDao = new UserDao();
    return userDao.getUser(email, password);

  }

  @Override
  public boolean createUser(String name, String email, String password) {
    UserDao userDao = new UserDao();
    User user = new User(String.valueOf(Math.random() * 10000), name, email,
        password);
    User createdUser = userDao.createUser(user);
    if (createdUser != null) {
      System.out.println("User Created !");
      return true;
    }
    System.out.println("Aleardy exist");

    return false;
  }

  @Override
  public List<User> findAllUsers() {
    UserDao userDao = new UserDao();
    return userDao.getAllUsers();
  }

  @Override
  public List<User> findUsersByParams(String searchText) {
    UserDao userDao = new UserDao();
    return userDao.getUsersByParams(searchText);
  }

  @Override
  public User getUserById(String id) {
    UserDao userDao = new UserDao();
    return userDao.getUserById(id);
  }

}
