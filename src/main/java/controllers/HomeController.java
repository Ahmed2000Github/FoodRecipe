package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.JMSException;

import javax.faces.event.ActionEvent;
import lombok.Data;
import models.RecipeVM;
import models.User;
import services.IRecipeServices;
import services.JMSServices.IMessageSender;

@Named
@SessionScoped
@Data
public class HomeController implements Serializable {
    private String message;
    private String current = "home";
    @EJB
    private IMessageSender messageSender;
    @EJB
    private IRecipeServices recipeServices;

    public void sendFeedback(ActionEvent ae) throws JMSException {
        System.out.println("######### Sending feedback ...");
        User user = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user");
        messageSender.sendFeedbacks(user, message);
        this.message = "";
    }

    public String getCSSClasse(String item) {

        if (item.equals(current)) {
            return "text-primary";
        }
        return "";
    }

    public void goTo(String url, String item) throws IOException {
        System.out.println("get current classe : " + current + "_____ " + item);
        this.current = item;
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public List<RecipeVM> getFirst() {
        return recipeServices.getTopRecipes().first;
    }

    public List<RecipeVM> getSecond() {
        return recipeServices.getTopRecipes().second;
    }
}
