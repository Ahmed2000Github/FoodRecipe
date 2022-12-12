package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.jms.JMSException;
import lombok.Data;
import models.User;
import services.IRecipeDetailsServices;
import services.JMSServices.CustomMessage;
import services.JMSServices.IMessageReceiver;
import services.JMSServices.IMessageSender;

@Named
@SessionScoped
@Data
public class RecipesDetailsController implements Serializable {

    private String id = "6341bafbb6a9870b7aa60dae";
    Map<String, String> infoData;
    private Map<String, Map<String, Map<String, String>>> ingredientData;
    private List<Map<String, String>> stepData;
    private Map<String, String> nutritionData;
    private List<Map<String, String>> commentsData;
    private String newComment;
    private List<CustomMessage> messagesList = new ArrayList<>();

    @EJB
    private IRecipeDetailsServices recipeDetailsServices;

    @EJB
    private IMessageSender messageSender;
    @EJB
    private IMessageReceiver messageReceiver;

    public void setRecipeId(String id) throws IOException {

        this.id = id;
        fillData();
        FacesContext.getCurrentInstance().getExternalContext().redirect("recipeDetails.xhtml");
    }

    public void fillData() {
        infoData = recipeDetailsServices.getInformations(id);
        ingredientData = recipeDetailsServices.getIngridients(id);
        stepData = recipeDetailsServices.getSteps(id);
        nutritionData = recipeDetailsServices.getNutritions(id);
        commentsData = recipeDetailsServices.getComments(id);
    }

    public List<CustomMessage> getMessages() throws JMSException {

        // messagesList.add(new CustomMessage("id", "id", "id", "newComment", "date"));
        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user");
        String userId = user.getId();
        messagesList = messageReceiver.receiveNewLike(userId);
        return messagesList;
    }

    public void refreshMessages() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("notifications.xhtml");
    }

    public void addLike() throws JMSException {
        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user");
        String userId = user.getId();
        String userName = user.getName();
        recipeDetailsServices.addLike(id, userId);
        messageSender.sendNewLike(infoData.get("ownerId"), infoData.get("title"), userName);
        System.out.println("################################# " + infoData.get("ownerId"));
    }

    public void addComment(ActionEvent ae) throws IOException {

        User contextPrincipe = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user");
        recipeDetailsServices.addComment(id, contextPrincipe.getId(), newComment);
        newComment = "";
        fillData();
        FacesContext.getCurrentInstance().getExternalContext().redirect("recipeDetails.xhtml");
    }

}
