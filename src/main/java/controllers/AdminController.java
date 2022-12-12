
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

import models.FeedbackMessage;
import models.User;
import services.IAdminService;
import services.JMSServices.IMessageReceiver;

/**
 * AdminController
 */

@Named
@SessionScoped
public class AdminController implements Serializable {

    List<FeedbackMessage> messagesList = new ArrayList<>();
    List<Map<String, String>> recipesList = new ArrayList<>();
    List<Map<String, String>> commentsOfUserList = new ArrayList<>();
    List<Map<String, String>> commentsOfRecipeList = new ArrayList<>();
    List<User> usersList = new ArrayList<>();

    @EJB
    private IAdminService adminService;
    @EJB
    private IMessageReceiver messageReceiver;

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

    public void getRecipes(FeedbackMessage message) {
        recipesList = adminService.getAllRecipes();
    }

    public void getCommentsOfUser(User user) {
        commentsOfUserList = adminService.getAllCommentsOfUser(user.getId());
    }

    public void getUsers() {
        usersList = adminService.getAllUsers();
    }

    public void getCommentsOfRecipe(String recipeId) {
        commentsOfRecipeList = adminService.getAllCommentsOfRecipe(recipeId);
    }

    public void deleteComment(String commentId) {
        adminService.deleteComment(commentId);
    }

    public void deleteRecipe(String recipeId) {
        adminService.deleteComment(recipeId);
    }

    public void deleteUser(String userId) {
        adminService.deleteComment(userId);
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