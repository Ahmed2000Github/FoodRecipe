
package services;

import models.Pair;
import models.RecipeVM;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Local(IRecipeServices.class)
@Stateless

public class RecipeServices implements AutoCloseable, IRecipeServices {
    private final Driver driver;

    public RecipeServices() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));
    }

    @Override
    public List<RecipeVM> searchByType(String type) {
        if (type.equals("all")) {
            return getAll();
        }

        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe{type:'" + type
                    + "'}) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like";
            Result result = session.run(query);
            List<Record> list = result.list();
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (Record record : list) {
                query = "MATCH (:Recipe {id: '" + record.get("recipe.id").asString()
                        + "'})-[r:HAS_COMMENT]-(comment) RETURN count(r)";
                Result subResult = session.run(query);
                listRecipes.add(new RecipeVM(record.get("recipe.id").asString(), record.get("recipe.title").asString(),
                        record.get("recipe.url").asString(), String.valueOf(subResult.single().get("count(r)").asInt()),
                        String.valueOf(record.get("recipe.like").asInt()), record.get("recipe.date").asString()));
            }
            session.close();
            return listRecipes;
        }

    }

    @Override
    public List<RecipeVM> getAll() {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like";
            Result result = session.run(query);
            List<Record> list = result.list();
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (Record record : list) {
                query = "MATCH (:Recipe {id: '" + record.get("recipe.id").asString()
                        + "'})-[r:HAS_COMMENT]-(comment) RETURN count(r)";
                Result subResult = session.run(query);
                listRecipes.add(new RecipeVM(record.get("recipe.id").asString(), record.get("recipe.title").asString(),
                        record.get("recipe.url").asString(), String.valueOf(subResult.single().get("count(r)").asInt()),
                        String.valueOf(record.get("recipe.like").asInt()), record.get("recipe.date").asString()));
            }
            session.close();
            return listRecipes;
        }

    }

    @Override
    public void addRecipe(String title, String type, String url, Map<String, List<Map<String, String>>> ingredients,
            List<Map<String, String>> steps, List<Map<String, String>> nutritions, String userId) {

        if (url == null) {
            url = "../images/default.png";
        }
        Map<String, String> _nutritions = new HashMap<>();
        for (Map<String, String> item : nutritions) {
            _nutritions.putAll(item);
        }
        try (Session session = driver.session()) {
            String recipeId = UUID.randomUUID().toString() + "-recipe";
            String query = "CREATE (ee:Recipe {title: '" + title + "', type: '" + type
                    + "', url: '" + url + "',date:'" + DateFormat.getInstance().format(new Date())
                    + "', like:0,id:'" + recipeId + "'}) RETURN id(ee)";
            session.run(query);
            query = "MATCH (a:User),(b:Recipe) WHERE a.id = '" + userId + "' AND b.id = '" + recipeId
                    + "' CREATE (a)-[r:SHARE]->(b) RETURN r";
            session.run(query);
            addIngridient(ingredients, recipeId, session);
            addStep(steps, recipeId, session);
            addNutrition(_nutritions, recipeId, session);
            session.close();
        }

    }

    public void addIngridient(Map<String, List<Map<String, String>>> ingredients, String recipeId, Session session) {
        Set<String> keys = ingredients.keySet();
        for (String group : keys) {
            try {
                for (Map<String, String> map : ingredients.get(group)) {
                    String ingridientId = UUID.randomUUID().toString() + "-ingridient";
                    String query = "CREATE (ee:Ingridient {group:'" + group + "',name: '" + map.get("name")
                            + "', quantity: '" + map.get("quantity") + "',id:'" + ingridientId + "'}) RETURN id(ee)";
                    session.run(query);
                    query = "MATCH (a:Recipe),(b:Ingridient) WHERE a.id = '" + recipeId + "' AND b.id = '"
                            + ingridientId
                            + "' CREATE (a)-[r:HAS_INGRIDIENT]->(b) RETURN r";
                    session.run(query);
                }
            } catch (Exception e) {
            }

        }
    }

    public void addNutrition(Map<String, String> nutritions, String recipeId, Session session) {

        String query = "CREATE (ee:Nutrition {";
        Set<String> set = nutritions.keySet();
        for (String key : set) {
            query += key + ":'" + nutritions.get(key) + "',";
        }
        String nutritionId = UUID.randomUUID().toString() + "-nutrition";
        query += "id:'" + nutritionId + "'}) RETURN id(ee)";
        session.run(query);
        query = "MATCH (a:Recipe),(b:Nutrition) WHERE a.id = '" + recipeId + "' AND b.id = '" + nutritionId
                + "' CREATE (a)-[r:HAS_NUTRITION]->(b) RETURN r";
        session.run(query);
    }

    public void addStep(List<Map<String, String>> steps, String recipeId, Session session) {
        for (Map<String, String> map : steps) {
            String stepId = UUID.randomUUID().toString() + "-step";
            String query = "CREATE (ee:Step {num:'" + map.get("num") + "',description: '" + map.get("description")
                    + "', time: '" + map.get("time") + "',id:'" + stepId + "'}) RETURN id(ee)";
            session.run(query);
            query = "MATCH (a:Recipe),(b:Step) WHERE a.id = '" + recipeId + "' AND b.id = '" + stepId
                    + "' CREATE (a)-[r:HAS_STEP]->(b) RETURN r";
            session.run(query);
        }

    }

    @Override
    public List<RecipeVM> getByUserId(String userId) {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User {id:'" + userId
                    + "'})-[r:SHARE]->(recipe:Recipe) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like";
            Result result = session.run(query);
            List<Record> list = result.list();
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (Record record : list) {
                query = "MATCH (:Recipe {id: '" + record.get("recipe.id").asString()
                        + "'})-[r:HAS_COMMENT]-(comment) RETURN count(r)";
                Result subResult = session.run(query);
                listRecipes.add(new RecipeVM(record.get("recipe.id").asString(), record.get("recipe.title").asString(),
                        record.get("recipe.url").asString(), String.valueOf(subResult.single().get("count(r)").asInt()),
                        String.valueOf(record.get("recipe.like").asInt()), record.get("recipe.date").asString()));
            }
            session.close();
            return listRecipes;
        }
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    @Override
    public Pair<List<RecipeVM>, List<RecipeVM>> getTopRecipes() {

        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like LIMIT 3";
            Result result = session.run(query);
            List<Record> list = result.list();
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (Record record : list) {
                query = "MATCH (:Recipe {id: '" + record.get("recipe.id").asString()
                        + "'})-[r:HAS_COMMENT]-(comment) RETURN count(r)";
                Result subResult = session.run(query);
                listRecipes.add(new RecipeVM(record.get("recipe.id").asString(), record.get("recipe.title").asString(),
                        record.get("recipe.url").asString(), String.valueOf(subResult.single().get("count(r)").asInt()),
                        String.valueOf(record.get("recipe.like").asInt()), record.get("recipe.date").asString()));
            }
            session.close();
            return new Pair<List<RecipeVM>, List<RecipeVM>>(listRecipes, listRecipes);
        }

    }

    // public static void main(String[] args) {
    // RecipeServices services = new RecipeServices();
    // System.out.println("@@@@@@@@@@@@@@@@@@");
    // System.out.println(services.getTopRecipes().first.size());
    // // for (RecipeVM recipeVM : services.searchByType("juice")) {
    // // System.out.println(recipeVM.getComments());
    // // }
    // // System.out.println("Description Description Description D...".length());
    // // services.addRecipe();
    // }

}
