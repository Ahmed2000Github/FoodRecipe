package services;

import java.util.List;
import java.util.Map;

import models.Admin;
import models.FeedbackMessage;
import models.User;

public interface IAdminService {
    public Admin doLogin(String email, String password);

    // public boolean createUser(String name, String email, String password);

    public List<User> getAllUsers();

    public List<Map<String, String>> getAllCommentsOfRecipe(String recipeId);

    public List<Map<String, String>> getAllCommentsOfUser(String userId);

    public List<Map<String, String>> getAllRecipes();

    public void deleteComment(String commentId);

    public void deleteRecipe(String recipeId);

    public void deleteUser(String userId);

    public Map<String, String> getStatistics();

    public List<FeedbackMessage> getFeedbacks();

    public void storeFeedback(FeedbackMessage feedbackMessage);

    // public List<User> findUsersByParams(String searchText);

    // public User getUserById(String id);
}
