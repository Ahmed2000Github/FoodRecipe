package services;

import models.User;

import java.util.List;

public interface IUserService {
    public User doLogin(String email, String password);

    public boolean createUser(String name, String email, String password);

    public List<User> findAllUsers();

    public List<User> findUsersByParams(String searchText);

    public User getUserById(String id);

}
