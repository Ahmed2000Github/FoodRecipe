package controllers;

import models.User;
import models.UserRecipes;
import services.IRecipeServices;
import services.IUserService;
import services.JMSServices.IMessageReceiver;
import lombok.Data;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Named
@SessionScoped
@Data
public class UserController implements Serializable {
    @EJB
    private IUserService iUserService;
    @EJB
    private IRecipeServices recipeServices;
    @EJB
    private IMessageReceiver messageReceiver;
    private String displayStr = "";
    boolean auth = true;
    boolean createdBool = true;
    String name;
    String email;
    String password;
    String userShownId;
    String searchText = "";

    public void usersSize() throws Exception {

        System.out.println(email + " " + password);
        // FacesContext.getCurrentInstance().getExternalContext().redirect("affich.xhtml");
    }

    public void doLogin() throws Exception {
        // System.out.println("email : "+email+" ,password : "+password);
        FacesContext context = FacesContext.getCurrentInstance();
        User user = iUserService.doLogin(email, password);
        if (user != null) {
            context.getExternalContext().getSessionMap().put("user", user);
            name = user.getName();
            messageReceiver.receiveNewLike(user.getId());
            FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
            auth = true;
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            auth = false;
        }
    }

    public void createUser() throws Exception {
        boolean created = iUserService.createUser(name, email, password);
        if (created) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            createdBool = true;
        } else {
            FacesContext.getCurrentInstance().getExternalContext().redirect("register.xhtml");
            auth = false;
        }
    }

    // public void goToRegister() throws IOException {
    // FacesContext.getCurrentInstance().getExternalContext().redirect("register.xhtml");
    // }

    public void logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
    }

    public void testSession() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getSessionMap().get("user") != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("home.xhtml");
        }
    }

    public void testSessionHome() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getSessionMap().get("user") == null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
        }
    }

    public List<User> getAllUsers() {
        // if (searchText.equals("")) {
        // System.out.println("no search type !!!!!!!!!!!!! ");
        // return iUserService.findAllUsers();
        // } else {
        // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ " + searchText);
        List<User> result = iUserService.findUsersByParams(searchText);
        // for (User user : result) {
        // System.out.println("affichage : " + user.getEmail());
        // }
        // searchText = "";
        return result;
        // }

    }

    public void searchSubmit() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("users.xhtml");
    }

    public void handleShownUser(String userShownId) throws IOException {
        this.userShownId = userShownId;
        System.out.println("Shown User Id : " + userShownId);
        FacesContext.getCurrentInstance().getExternalContext().redirect("userRecipeList.xhtml");
    }

    public UserRecipes fetchShownUser() {
        UserRecipes userRecipes = new UserRecipes();
        AtomicInteger allLikesCount = new AtomicInteger();
        AtomicInteger allCommentsCount = new AtomicInteger();
        recipeServices.getByUserId(userShownId).stream().forEach(recipe -> {
            allLikesCount.set(allLikesCount.get() + Integer.parseInt(recipe.getLikes()));
        });
        recipeServices.getByUserId(userShownId).stream().forEach(recipe -> {
            allCommentsCount.set(allCommentsCount.get() + Integer.parseInt(recipe.getComments()));
        });
        userRecipes.setUser(iUserService.getUserById(userShownId));
        userRecipes.setRecipes(recipeServices.getByUserId(userShownId));
        userRecipes.setAllLikes(allLikesCount.get());
        userRecipes.setAllComments(allCommentsCount.get());
        userRecipes.setRecipesCount(recipeServices.getByUserId(userShownId).size());
        // userRecipes.setRecipes(new ArrayList<>());
        return userRecipes;
    }
}
