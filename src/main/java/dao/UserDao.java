package dao;

import models.RecipeVM;
import models.User;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao implements AutoCloseable {
    private final Driver driver;

    public UserDao() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));

    }

    public User getUser(String email, String password) {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) WHERE user.email = '" + email
                    + "' RETURN user.id,user.password,user.email,user.name";
            System.out.println("####" + email + "#####");
            Result result = session.run(query);
            User user = new User();
            try {
                Record record = result.single();
                if (password.equals(record.get("user.password").asString())) {
                    user.setId(record.get("user.id").asString());
                    user.setName(record.get("user.name").asString());
                    user.setEmail(record.get("user.email").asString());
                    user.setPassword(record.get("user.password").asString());
                    System.out.println(user.getPassword());
                    return user;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("I didn't find it");
            return null;
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) RETURN user.id,user.name,user.email,user.password";
            Result result = session.run(query);
            List<Record> list = result.list();
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (Record record : list) {
                query = "MATCH (:Recipe {id: '" + record.get("recipe.id").asString()
                        + "'})-[r:HAS_COMMENT]-(comment) RETURN count(r)";
                Result subResult = session.run(query);
                userList.add(new User(record.get("user.id").asString(), record.get("user.name").asString(),
                        record.get("user.email").asString(), record.get("user.password").asString()));
            }
            session.close();
            return userList;
        }

    }

    public User createUser(User user) {
        try (Session session = driver.session()) {
            String userId = UUID.randomUUID().toString() + "-user";
            String query = " MATCH (user:User) WHERE user.email = '" + user.getEmail() + "' RETURN user";
            List<Record> list = session.run(query).list();

            if (list.size() == 0) {
                query = "CREATE (user:User {name: '" + user.getName() + "', email: '" + user.getEmail()
                        + "', password: \"" + user.getPassword() + "\",id:'"
                        + userId + "'})";
                session.run(query);
                user.setId(userId);
                return user;
            } else
                return null;
        }
    }

    public void deleteUser(User user) {

    }

    @Override
    public void close() throws Exception {
        driver.close();

    }

    public User getUserById(String id)  {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) WHERE user.id = '" + id
                    + "' RETURN user.id,user.password,user.email,user.name";
            Result result = session.run(query);
            User user = new User();
            try {
                Record record = result.single();
                user.setId(record.get("user.id").asString());
                user.setName(record.get("user.name").asString());
                user.setEmail(record.get("user.email").asString());
                user.setPassword(record.get("user.password").asString());
                System.out.println(user.getPassword());
                return user;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("I didn't find it");
            return null;
        }
    }

//    public static void main(String[] args) {
//        UserDao userDao = new UserDao();
//        System.out.println("ssssssssssssssssssssss");
//        List<User> allUsers = userDao.getUsersByParams("a");
//        System.out.println(allUsers.size());
//
//    }

    public List<User> getUsersByParams(String searchText) {
        // System.out.println("############################# " + searchText);
        List<User> userList = new ArrayList<>();
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) WHERE user.name  =~ '.*(?i)" + searchText
                    + ".*' OR user.email  =~ '.*(?i)"
                    + searchText + ".*' RETURN user.id,user.name,user.email,user.password";
            Result result = session.run(query);
            List<Record> list = result.list();
            for (Record record : list) {
                // System.out.println("$$$$$$$$$$$$$$$$ " +
                // record.get("user.email").asString());
                userList.add(new User(record.get("user.id").asString(), record.get("user.name").asString(),
                        record.get("user.email").asString(), record.get("user.password").asString()));
            }
            session.close();
            return userList;
        }

    }
}
