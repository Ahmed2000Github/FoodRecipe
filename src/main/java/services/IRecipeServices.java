package services;

import java.util.List;
import java.util.Map;

import models.RecipeVM;

public interface IRecipeServices {
    public List<RecipeVM> searchByType(String type);

    public List<RecipeVM> getAll();

    public void addRecipe(String title, String type, String url, Map<String, List<Map<String, String>>> ingredients,
            List<Map<String, String>> steps, List<Map<String, String>> nutritions, String userId);

    public List<RecipeVM> getByUserId(String userId);
}
