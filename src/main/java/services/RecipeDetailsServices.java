package services;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import javax.ejb.Local;
import javax.ejb.Stateless;

@Local(IRecipeDetailsServices.class)
@Stateless
public class RecipeDetailsServices implements AutoCloseable, IRecipeDetailsServices {
    private final Driver driver;

    public RecipeDetailsServices() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));

    }

    @Override
    public Map<String, String> getInformations(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (user:User)-[r:SHARE]->(recipe:Recipe {id:'" + id
                    + "'}) RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like,user.name,user.id";
            List<Record> list = session.run(query).list();
            Map<String, String> data = new HashMap<>();

            data.put("_id", list.get(0).get("recipe.id").asString());
            data.put("title", list.get(0).get("recipe.title").asString());
            data.put("url", list.get(0).get("recipe.url").asString());
            data.put("type", list.get(0).get("recipe.type").asString());
            data.put("likes", String.valueOf(list.get(0).get("recipe.like").asInt()));
            data.put("date", list.get(0).get("recipe.date").asString());
            data.put("owner", list.get(0).get("user.name").asString());
            data.put("ownerId", list.get(0).get("user.id").asString());
            query = "MATCH (:Recipe {id: '" + id + "'})-[r:HAS_INGRIDIENT]-(ingridient) RETURN count(r)";
            list = session.run(query).list();
            data.put("ingredientsCounter", String.valueOf(list.get(0).get("count(r)").asInt()));
            query = "MATCH (:Recipe {id: '" + id + "'})-[r:HAS_STEP]-(step) RETURN count(r)";
            list = session.run(query).list();
            data.put("stepsCounter", String.valueOf(list.get(0).get("count(r)").asInt()));
            session.close();
            return data;
        }

    }

    @Override
    public Map<String, Map<String, Map<String, String>>> getIngridients(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id:'" + id
                    + "'})-[r:HAS_INGRIDIENT]->(ing:Ingridient) RETURN ing.group, ing.name,ing.quantity";
            System.out.println(query);
            List<Record> list = session.run(query).list();
            Map<String, Map<String, Map<String, String>>> data = new HashMap<>();

            int counter = 0;
            for (Record record : list) {
                String group = record.get("ing.group").asString();
                Map<String, Map<String, String>> element = new HashMap<>();
                if (data.containsKey(group)) {
                    element = data.get(group);
                }
                Map<String, String> prop = new HashMap<>();
                prop.put("name", record.get("ing.name").asString());
                prop.put("quantity", record.get("ing.quantity").asString());
                element.put("element" + counter, prop);
                counter++;
                data.put(group, element);

            }
            session.close();
            return data;
        }
    }

    @Override
    public List<Map<String, String>> getSteps(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id:'" + id
                    + "'})-[r:HAS_STEP]->(step:Step) RETURN step.num, step.description,step.time";
            System.out.println(query);
            List<Record> list = session.run(query).list();
            Map<String, Map<String, String>> data = new HashMap<>();

            int counter = 0;
            for (Record record : list) {
                Map<String, String> prop = new HashMap<>();
                prop.put("num", record.get("step.num").asString());
                prop.put("description", record.get("step.description").asString());
                prop.put("time", record.get("step.time").asString());
                data.put("step" + counter, prop);
                counter++;
            }
            session.close();
            return sortStep(data);
        }
    }

    @Override
    public List<Map<String, String>> sortStep(Map<String, Map<String, String>> data) {
        int index = 1;
        List<Map<String, String>> list = new ArrayList<>();
        Set<String> sets = data.keySet();
        for (int i = 0; i < data.size(); i++) {
            for (String set : sets) {
                int current = Integer.parseInt(data.get(set).get("num"));
                if (index == current) {
                    list.add(data.get(set));
                    index++;
                }
            }
        }
        return list;
    }

    @Override
    public Map<String, String> getNutritions(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id:'" + id + "'})-[r:HAS_NUTRITION]->(nut:Nutrition) RETURN nut.id";
            Result result = session.run(query);
            String nutId = result.single().get("nut.id").asString();
            query = "MATCH (n {id:'" + nutId
                    + "'} ) WITH keys(n) AS p UNWIND p AS x WITH DISTINCT x WHERE x =~ \".*\" RETURN collect(x);";
            System.out.println(query);
            List<Record> list = session.run(query).list();
            List<Object> listObj = list.get(0).get("collect(x)").asList();
            String attrs = "nut.id";
            for (Object object : listObj) {
                String attr = object.toString();
                if (!attr.equals("id")) {
                    attrs += ",nut." + attr;
                }
            }
            query = "MATCH (recipe:Recipe {id:'" + id + "'})-[r:HAS_NUTRITION]->(nut:Nutrition) RETURN " + attrs;
            list = session.run(query).list();
            Map<String, String> data = new HashMap<>();
            for (Object object : listObj) {
                String attr = object.toString();
                if (!attr.equals("id")) {
                    data.put(attr, list.get(0).get("nut." + attr).asString());

                }
            }
            session.close();
            return data;
        }
    }

    @Override
    public List<Map<String, String>> getComments(String id) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id: '" + id
                    + "'})-[r:HAS_COMMENT]-(comment:Comment) RETURN comment.id,comment.date,comment.description";
            List<Map<String, String>> data = new ArrayList<>();
            List<Record> list = session.run(query).list();
            for (Record record : list) {
                Map<String, String> comment = new HashMap<>();
                comment.put("date", record.get("comment.date").asString());
                comment.put("description", record.get("comment.description").asString());
                query = "MATCH (user:User )-[r:WRITE_COMMENT]-(comment:Comment {id:'" + record.get("comment.id")
                        .asString() + "'}) RETURN user.name";
                Result result = session.run(query);
                comment.put("username", result.single().get("user.name").asString());
                data.add(comment);
            }
            session.close();
            return data;

        }
    }

    @Override
    public void addComment(String recipeId, String userId, String description) {

        try (Session session = driver.session()) {
            String commentId = UUID.randomUUID().toString() + "-comment";
            String query = "CREATE (ee:Comment {date:'" + DateFormat.getInstance().format(new Date())
                    + "',description: '"
                    + description
                    + "',id:'" + commentId + "'}) RETURN id(ee)";
            session.run(query);
            query = "MATCH (a:Recipe),(b:Comment) WHERE a.id = '" + recipeId + "' AND b.id = '" + commentId
                    + "' CREATE (a)-[r:HAS_COMMENT]->(b) RETURN r";
            session.run(query);
            query = "MATCH (a:User),(b:Comment) WHERE a.id = '" + userId
                    + "' AND b.id = '" + commentId + "' CREATE (a)-[r:WRITE_COMMENT]->(b) RETURN r";
            session.run(query);
            session.close();
        }

    }

    @Override
    public void addLike(String recipeId, String userId) {

        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe {id:'" + recipeId
                    + "'}) RETURN recipe.like";
            Record record = session.run(query).single();
            int likes = record.get("recipe.like").asInt();
            likes++;
            query = "MATCH (recipe:Recipe {id:'" + recipeId
                    + "'}) SET recipe.like=" + likes + " RETURN recipe";
            session.run(query);
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws Exception {
        driver.close();

    }

    // public static void main(String[] args) {
    // ObjectId id = new ObjectId("6345bbb929966347c313d8f6");
    // // ObjectId userId = new ObjectId("634050eb928c4d0152f09518");
    // RecipeDetailsServices service = new RecipeDetailsServices();
    // Map<String, Map<String, String>> data = service.getSteps(id);
    // List<Map<String, String>> donnees = service.sortStep(data);
    // try {
    // System.out.println("##################################");
    // for (Map<String, String> map : donnees) {
    // System.out.println(map.get("num"));
    // }
    // } catch (Exception e) {
    // // TODO: handle exception
    // }

    // }

}
