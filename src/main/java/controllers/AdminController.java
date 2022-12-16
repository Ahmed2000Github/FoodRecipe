
package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.JMSException;

import lombok.Data;
import models.FeedbackMessage;
import models.User;
import services.IAdminService;
import services.JMSServices.IMessageReceiver;

/**
 * AdminController
 */

@Named
@SessionScoped
@Data
public class AdminController implements Serializable {

    private List<FeedbackMessage> messagesList = new ArrayList<>();
    private List<Map<String, String>> recipesList = new ArrayList<>();
    private List<Map<String, String>> commentsOfUserList = new ArrayList<>();
    private List<Map<String, String>> commentsOfRecipeList = new ArrayList<>();
    private List<User> usersList = new ArrayList<>();
    private int counter = 0;
    private int commentCounter = 0;

    @EJB
    private IAdminService adminService;
    @EJB
    private IMessageReceiver messageReceiver;

    // public AdminController() {
    // super();
    // recipesList = adminService.getAllRecipes();
    // usersList = adminService.getAllUsers();
    // }

    public void getMessage() throws JMSException {
        List<FeedbackMessage> list = messageReceiver.receiveFeedback();
        if (list.size() > 0) {
            for (FeedbackMessage feedbackMessage : list) {
                if (!messagesList.contains(feedbackMessage)) {
                    messagesList.add(feedbackMessage);
                }
            }
        }
    }

    public void addMessage(FeedbackMessage message) {
        adminService.storeFeedback(message);
        messagesList.remove(message);
    }

    public List<Map<String, String>> getRecipes() {
        counter = 0;
        return adminService.getAllRecipes();
    }

    public int _getCounter() {
        counter++;
        return counter;
    }

    public int _getCommentCounter() {
        commentCounter++;
        return commentCounter;
    }

    public void getCommentsOfUser(User user) {
        commentCounter = 0;
        commentsOfUserList = adminService.getAllCommentsOfUser(user.getId());
    }

    public List<User> getUsers() {
        counter = 0;
        return adminService.getAllUsers();
    }

    public void getCommentsOfRecipe(String recipeId) {
        commentCounter = 0;
        commentsOfRecipeList = adminService.getAllCommentsOfRecipe(recipeId);
    }

    public void deleteComment(Map<String, String> comment) {
        commentsOfRecipeList.remove(comment);
        commentsOfUserList.remove(comment);
        System.out.println("delete comment of id : " + comment.get("id"));
        adminService.deleteComment(comment.get("id"));
    }

    public void deleteRecipe(String recipeId) {
        adminService.deleteRecipe(recipeId);
    }

    public Map<String, Integer> getStatistics() {
        return adminService.getStatistics();
    }

    public void deleteUser(String userId) {
        adminService.deleteUser(userId);
    }

    public void refreshMessages() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("./notifications.xhtml");
    }

    public void refreshRecipes() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("./recipes&Comments.xhtml");
    }

    public void refreshUsers() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("./users&Comments.xhtml");
    }

}