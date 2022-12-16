package controllers;

import lombok.Data;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@Data
public class Principal implements Serializable {
    String name;
    String email;
    String password;

}
