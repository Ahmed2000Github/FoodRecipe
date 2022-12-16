package models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@RequestScoped
public class User implements Serializable {
    private static final long serialVersionUID = -7250065889869767422L;

    String id;
    String name;
    String email;
    String password;

    public void nameValidation(FacesContext context, UIComponent comp,
            Object value) throws IOException {

        String name = (String) value;
        String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (name.length() < 4) {
            ((UIInput) comp).setValid(false);
            FacesMessage message = new FacesMessage(
                    "Minimum length of model name is 4");
            context.addMessage(comp.getClientId(context), message);
        } else if (matcher.matches()) {
            ((UIInput) comp).setValid(false);
            FacesMessage message = new FacesMessage(
                    "les caractères spéciaux ne sont pas autorisé !");
            context.addMessage(comp.getClientId(context), message);
        }

    }

    public void emailValidation(FacesContext context, UIComponent comp,
            Object value) throws IOException {

        String email = (String) value;

        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            ((UIInput) comp).setValid(false);
            FacesMessage message = new FacesMessage(
                    "Votre Email n'est pas valid");
            context.addMessage(comp.getClientId(context), message);
        }

    }

    public void passwordValidation(FacesContext context, UIComponent comp,
            Object value) throws IOException {

        /*
         * String password = (String) value;
         * String regex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}";
         * boolean valid=isValidPassword(password,regex);
         * if (!valid) {
         * ((UIInput) comp).setValid(false);
         * FacesMessage message = new FacesMessage(
         * "Try again");
         * context.addMessage(comp.getClientId(context), message);
         * }
         */

    }

    public static boolean isValidPassword(String password, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
