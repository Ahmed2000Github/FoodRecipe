package controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import lombok.Data;
import models.User;
import services.IRecipeServices;

@Named
@SessionScoped
@Data

public class AddRecipeController implements Serializable {
    private static final long serialVersionUID = 2729758432756108274L;

    @EJB
    private IRecipeServices recipeServices;
    private String title;
    private String folder = "D:\\apache-tomee-plume-8.0.12\\webapps\\images\\";
    private String type;
    private String url;
    private String groupType;
    private String fileName;
    private String name = "";
    private String quantity = "";
    private String description = "";
    private String time = "";
    private String nameN = "";
    private String value = "";
    private String groupName = "";
    private Part imageFile;
    private Map<String, List<Map<String, String>>> ingredients = new HashMap<>();

    private List<Map<String, String>> nutritions = new ArrayList<>();
    private List<Map<String, String>> steps = new ArrayList<>();

    AddRecipeController() {
        if (ingredients.isEmpty()) {
            ingredients.put("others", null);
        }
    }

    public List<String> getKeys() throws InterruptedException {
        return new ArrayList<String>(ingredients.keySet());
    }

    public List<Map<String, String>> getValues(String group) {
        // System.out.println("sssssssssssssssssssssssssssssss "+" "+group);
        // List<Map<String,String>> list = new ArrayList<>();
        // List<Map<String,String>> listO = ingredients.get(group);
        // if(listO != null) {
        // for(Map<String,String> map:listO) {
        // list.add(map);
        // }
        // }

        return ingredients.get(group);
    }

    public void changeFileName() {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");

        if (imageFile != null) {
            this.fileName = getFilename(imageFile);
            System.out.println("saveFile method invoked..");
            System.out.println("content-type:{0}" + imageFile.getContentType());
            System.out.println("filename:{0}" + imageFile.getName());
            System.out.println("submitted filename:{0}" + imageFile.getSubmittedFileName());
            System.out.println("size:{0}" + imageFile.getSize());
            try {
                Long time = new Date().getTime();
                url = folder + (time) + fileName;
                imageFile.write(url);
                url = "../images/" + (time) + fileName;
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

    }

    public void redirect() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("addRecipe.xhtml");
        } catch (Exception e) {

        }
    }

    public void addIngredient() {
        // System.out.println("wwwwwwwwwwww");
        if (name != null && !name.trim().equals("")) {
            List<Map<String, String>> list = ingredients.get(groupType);
            if (list == null) {
                list = new ArrayList<>();
            }
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("quantity", quantity);
            list.add(map);
            // ingredients.remove(groupType);
            ingredients.put(groupType, list);
            name = "";
            quantity = "";
        }

        changeFileName();
        redirect();
    }

    public void addStep() {
        System.out.println("wwwwwwwwwwww");
        if (description != null && !description.trim().equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("num", String.valueOf(steps.size() + 1));
            map.put("description", description);
            map.put("time", time);
            steps.add(map);
            description = "";
            time = "";
        }

        changeFileName();
        redirect();
    }

    public void moveUpStep(Map<String, String> map) {
        System.out.println("wwwwwwwwwwww");
        int index = steps.indexOf(map);
        if (index > 0) {
            Map<String, String> mapSwp = steps.get(index - 1);
            mapSwp.remove("num");
            mapSwp.put("num", String.valueOf(index + 1));
            map.remove("num");
            map.put("num", String.valueOf(index));
            steps.set(index, mapSwp);
            steps.set(index - 1, map);
        }

        changeFileName();
        redirect();
    }

    public void deleteStep(Map<String, String> map) {
        System.out.println("wwwwwwwwwwww");
        steps.remove(map);

        changeFileName();
        redirect();
    }

    public void moveDownStep(Map<String, String> map) {
        System.out.println("wwwwwwwwwwww");
        int index = steps.indexOf(map);
        if (index < steps.size() - 1) {
            Map<String, String> mapSwp = steps.get(index + 1);
            mapSwp.remove("num");
            mapSwp.put("num", String.valueOf(index + 1));
            map.remove("num");
            map.put("num", String.valueOf(index + 2));
            steps.set(index, mapSwp);
            steps.set(index + 1, map);
        }

        changeFileName();
        redirect();
    }

    public void addNutrition() {
        System.out.println("wwwwwwwwwwww");
        if (nameN != null && !nameN.trim().equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put(nameN, value);
            nutritions.add(map);
            nameN = "";
            value = "";
        }

        changeFileName();
        redirect();
    }

    public void deleteNutrition(Map<String, String> map) {

        nutritions.remove(map);
        changeFileName();
        redirect();
    }

    public void addGroup() throws InterruptedException {
        if (groupName != null && !groupName.equals("")) {
            if (!ingredients.containsKey(groupName)) {
                this.ingredients.put(groupName, null);
            }

            groupName = "";
        }
        changeFileName();
        redirect();
    }

    public void deleteGroup(String key) {
        ingredients.remove(key);
        changeFileName();
        redirect();
    }

    public void deleteIngredient(String key, Map<String, String> map) {
        List<Map<String, String>> list = ingredients.get(key);
        list.remove(map);
        ingredients.remove(key);
        ingredients.put(key, list);
        changeFileName();
        redirect();
    }

    public void submit(ActionEvent ae) throws IOException {

        User contextPrincipe = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user");
        changeFileName();
        recipeServices.addRecipe(title, type, url, ingredients, steps, nutritions, contextPrincipe.getId());
        title = "";
        type = "";
        fileName = "";
        ingredients = new HashMap<>();
        ingredients.put("others", null);
        nutritions = new ArrayList<>();
        steps = new ArrayList<>();
        FacesContext.getCurrentInstance().getExternalContext().redirect("recipes.xhtml");
    }

    private static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE
                                                                                                                    // fix.
            }
        }
        return null;
    }

}
