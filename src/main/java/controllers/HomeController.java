package controllers;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.JMSException;

import javax.faces.event.ActionEvent;
import lombok.Data;
import models.User;
import services.JMSServices.IMessageSender;

@Named("homeController")
@SessionScoped
@Data
public class HomeController implements Serializable {
    private String message;
    private String current = "home";
    @EJB
    private IMessageSender messageSender;

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
}
