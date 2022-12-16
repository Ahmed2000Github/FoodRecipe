package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;

import models.Admin;
import models.FeedbackMessage;
import models.RecipeVM;
import models.User;

@Local(IAdminService.class)
@Stateless
public class AdminService implements IAdminService, AutoCloseable {
    private final Driver driver;

    public AdminService() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));

    }

    @Override
    public Admin doLogin(String email, String password) {
        return null;
    }

    @Override
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

    @Override
    public List<Map<String, String>> getAllCommentsOfRecipe(String recipeId) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id: '" + recipeId
                    + "'})-[r:HAS_COMMENT]-(comment:Comment) RETURN comment.id,comment.date,comment.description";
            List<Map<String, String>> data = new ArrayList<>();
            List<Record> list = session.run(query).list();
            for (Record record : list) {
                Map<String, String> comment = new HashMap<>();
                comment.put("id", record.get("comment.id").asString());
                comment.put("date", record.get("comment.date").asString());
                comment.put("description", record.get("comment.description").asString());
                query = "MATCH (user:User )-[r:WRITE_COMMENT]-(comment:Comment {id:'" + record.get("comment.id")
                        .asString() + "'}) RETURN user.name";
                Result result = session.run(query);
                comment.put("username", result.single().get("user.name").asString());
                data.add(comment);
            }
            session.close();
            return data;

        }
    }

    @Override
    public List<Map<String, String>> getAllCommentsOfUser(String userId) {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User {id: '" + userId
                    + "'})-[r:WRITE_COMMENT]-(comment:Comment) RETURN comment.id,comment.date,comment.description,user.name";
            List<Map<String, String>> data = new ArrayList<>();
            List<Record> list = session.run(query).list();
            for (Record record : list) {
                Map<String, String> comment = new HashMap<>();
                comment.put("id", record.get("comment.id").asString());
                comment.put("date", record.get("comment.date").asString());
                comment.put("description", record.get("comment.description").asString());
                comment.put("username", record.get("user.name").asString());
                data.add(comment);
            }
            session.close();
            return data;

        }
    }

    @Override
    public List<Map<String, String>> getAllRecipes() {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User)-[r:SHARE]->(recipe:Recipe ) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like,user.name,user.id";
            List<Record> list = session.run(query).list();
            List<Map<String, String>> dataList = new ArrayList<>();
            for (Record record : list) {
                Map<String, String> data = new HashMap<>();
                String recipeId = record.get("recipe.id").asString();
                data.put("id", recipeId);
                data.put("title", record.get("recipe.title").asString());
                data.put("url", record.get("recipe.url").asString());
                data.put("type", record.get("recipe.type").asString());
                data.put("likes", String.valueOf(record.get("recipe.like").asInt()));
                data.put("date", record.get("recipe.date").asString());
                data.put("owner", record.get("user.name").asString());
                data.put("ownerId", record.get("user.id").asString());
                query = "MATCH (:Recipe {id: '" + recipeId + "'})-[r:HAS_INGRIDIENT]-(ingridient) RETURN count(r)";
                list = session.run(query).list();
                data.put("ingredientsCounter", String.valueOf(list.get(0).get("count(r)").asInt()));
                query = "MATCH (:Recipe {id: '" + recipeId + "'})-[r:HAS_STEP]-(step) RETURN count(r)";
                list = session.run(query).list();
                data.put("stepsCounter", String.valueOf(list.get(0).get("count(r)").asInt()));
                dataList.add(data);
            }

            session.close();

            return dataList;
        }
    }

    @Override
    public void deleteComment(String commentId) {
        try (Session session = driver.session()) {
            String query = "MATCH (comment:Comment  {id: '" + commentId
                    + "'}) DETACH DELETE comment";
            session.run(query);

            session.close();
        }
    }

    @Override
    public void deleteRecipe(String recipeId) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe  {id: '" + recipeId
                    + "'})-[r:HAS_INGRIDIENT]-(ingridient:Ingridient) DETACH DELETE ingridient";
            session.run(query);
            query = "MATCH (recipe:Recipe  {id: '" + recipeId + "'})-[r:HAS_STEP]-(step:Step) DETACH DELETE step";
            session.run(query);
            query = "MATCH (recipe:Recipe  {id: '" + recipeId
                    + "'})-[r:HAS_NUTRITION]-(nutrition:Nutrition) DETACH DELETE nutrition";

            query = "MATCH (recipe:Recipe  {id: '" + recipeId
                    + "'})-[r:HAS_COMMENT]-(comment:Comment) DETACH DELETE comment";
            session.run(query);
            query = "MATCH (user:User  )-[r:SHARE]-(recipe:Recipe {id: '" + recipeId
                    + "'}) DETACH DELETE recipe";
            session.run(query);

            session.close();
        }
    }

    // public static void main(String[] args) {
    // new
    // AdminService().deleteRecipe("03d8c541-242a-4d45-b3a4-9c8f8f05ba33-recipe");
    // }

    @Override
    public void deleteUser(String userId) {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User {id: '" + userId
                    + "'})-[r:SHARE]-(recipe:Recipe) RETURN recipe.id ";
            Result result = session.run(query);
            List<Record> list = result.list();
            for (Record record : list) {
                this.deleteRecipe(record.get("recipe.id").asString());
            }
            query = "MATCH (user:User {id: '" + userId
                    + "'})-[r:WRITE_COMMENT]-(comment:Comment) DETACH DELETE comment";
            session.run(query);
            query = "MATCH (user:User {id: '" + userId
                    + "'}) DETACH DELETE user";
            session.run(query);

            session.close();
        }
    }

    @Override
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> data = new HashMap<>();
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) RETURN count(user)";
            Record record = session.run(query).single();
            data.put("users", record.get("count(user)").asInt());
            query = "MATCH (recipe:Recipe) RETURN count(recipe)";
            record = session.run(query).single();
            data.put("recipes", record.get("count(recipe)").asInt());
            query = "MATCH (comment:Comment) RETURN count(comment)";
            record = session.run(query).single();
            data.put("comments", record.get("count(comment)").asInt());
            query = "MATCH (feedback:Feedback) RETURN count(feedback)";
            record = session.run(query).single();
            data.put("feedbacks", record.get("count(feedback)").asInt());
            session.close();

            return data;
        }
    }

    // public static void main(String[] args) {
    // System.out.println(new AdminService().getStatistics());
    // }

    @Override
    public List<FeedbackMessage> getFeedbacks() {
        List<FeedbackMessage> feedbackList = new ArrayList<>();
        try (Session session = driver.session()) {
            String query = "MATCH (user:User) -[r:ADD_FEEDBACK]->(feedback:Feedback)  RETURN user.id,user.name,user.email,feedback.id,feedback.description,feedback.date";
            Result result = session.run(query);
            List<Record> list = result.list();
            for (Record record : list) {

                feedbackList.add(new FeedbackMessage(record.get("feedback.id").asString(),
                        record.get("user.id").asString(), record.get("user.name").asString(),
                        record.get("user.email").asString(), record.get("feedback.date").asString(),
                        record.get("feedback.description").asString()));
            }
            session.close();
            return feedbackList;
        }
    }

    @Override
    public void storeFeedback(FeedbackMessage feedbackMessage) {
        try (Session session = driver.session()) {

            String query = "CREATE (ee:Feedback {id:'" + feedbackMessage.getId()
                    + "',description: '"
                    + feedbackMessage.getMessage()
                    + "',date:'" + feedbackMessage.getDate() + "'}) RETURN id(ee)";
            session.run(query);
            query = "MATCH (a:User),(b:Feedback) WHERE a.id = '" + feedbackMessage.getUserId()
                    + "' AND b.id = '" + feedbackMessage.getId() + "' CREATE (a)-[r:ADD_FEEDBACK]->(b) RETURN r";
            session.run(query);
            session.close();
        }
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

}
