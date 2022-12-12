package services;

import java.util.List;
import java.util.Map;

public interface IRecipeDetailsServices {
    public Map<String, String> getInformations(String id);

    public Map<String, Map<String, Map<String, String>>> getIngridients(String id);

    public List<Map<String, String>> getSteps(String id);

    public List<Map<String, String>> sortStep(Map<String, Map<String, String>> data);

    public Map<String, String> getNutritions(String id);

    public List<Map<String, String>> getComments(String id);

    public void addComment(String recipeId, String userId, String description);

    public void addLike(String recipeId, String userId);

}
