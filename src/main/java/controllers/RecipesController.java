package controllers;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

import models.RecipeVM;
import models.User;
import services.IRecipeServices;
import services.ISearchService;

@Named("recipesController")
@SessionScoped
public class RecipesController implements Serializable {

    String type = "all";
    String word = "";
    private String folder = "D:\\apache-tomee-plume\\webapps\\images\\";
    Boolean isOpen = false;
    private String fileName = "";
    String url = "";
    private Part imageFile;
    List<RecipeVM> recipes = new ArrayList<>();
    Map<String, String> results = new HashMap<>();
    @EJB
    private ISearchService searchService;
    @EJB
    private IRecipeServices recipeServices;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        System.out.println("the type is : " + type);
        this.type = type;
    }

    public String getCurrent(String income) {
        if (type.equals(income)) {
            return "active";
        }
        return "";
    }

    public List<RecipeVM> getRecipes()
            throws XPathExpressionException, ParserConfigurationException, SAXException,
            IOException, TransformerException {

        if (this.type.equals("owner")) {
            User contextPrincipe = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                    .get("user");

            return recipeServices.getByUserId(contextPrincipe.getId());
        } else {
            return recipeServices.searchByType(type.toLowerCase());
        }

    }

    public void getRecipeByWord(ActionEvent ae) {
        recipes = searchService.searchByWord(word);
    }

    public void getRecipeByImage(ActionEvent ae) {
        recipes = searchService.searchByImage(getFile());
        if (!url.equals("")) {
            File file = new File(url);
            file.delete();
            url = "";
        }
    }

    private File getFile() {
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
                File file = new File(url);
                return file;
            } catch (IOException ex) {
                System.out.println(ex);
                return null;
            }
        } else
            return null;

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
    // public static void main(String[] args) throws XPathExpressionException,
    // ParserConfigurationException, SAXException,
    // IOException, TransformerException {
    // System.out.println(getRecipes().get(0).getTitre());
    // ;
    // }

}
