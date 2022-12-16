package services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Record;

import models.RecipeVM;
import models.Pair;

@Local(ISearchService.class)
@Stateless
public class SearchService implements ISearchService, AutoCloseable {
    private final Driver driver;

    public SearchService() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));

    }

    @Override
    public List<RecipeVM> searchByWord(String word) {
        try (Session session = driver.session()) {
            String query = "MATCH (recipe:Recipe)WHERE recipe.title  =~ '.*(?i)" + word
                    + ".*' OR recipe.type  =~ '.*(?i)"
                    + word + ".*' OR recipe.date  =~ '.*(?i)"
                    + word + ".*' RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like";
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
    public Pair<List<RecipeVM>, Map<String, Double>> searchByImage(File image) {
        Map<String, Double> map = new HashMap<>();
        if (image != null) {
            System.out.println("image founded #######################");
            FileBody fileBody = new FileBody(image, ContentType.DEFAULT_BINARY);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("recipe", fileBody);
            HttpEntity entity = builder.build();

            HttpPost request = new HttpPost("http://127.0.0.1:5000/upload");
            request.setEntity(entity);

            HttpClient client = HttpClientBuilder.create().build();
            try {
                HttpResponse httpResponse = client.execute(request);
                String stringData = new String(httpResponse.getEntity().getContent().readAllBytes());
                stringData = stringData.replace("{", "");
                stringData = stringData.replace("}", "");
                String[] fields = stringData.split(",");

                for (String field : fields) {
                    String[] parts = field.replaceAll("\"", "").split(":");
                    map.put(parts[0], Double.parseDouble(parts[1]));
                }
                System.out.println("@@@@@@@@@@@@@@@");
                List<RecipeVM> list = this.searchByResult(map.keySet());

                return new Pair<List<RecipeVM>, Map<String, Double>>(list, map);
            } catch (IOException e) {
                e.printStackTrace();
                return new Pair<List<RecipeVM>, Map<String, Double>>(new ArrayList<>(), map);
            }
        }
        return new Pair<List<RecipeVM>, Map<String, Double>>(this.searchByWord(""), map);

    }

    public static void main(String[] args) {

        File file = new File("D:\\apache-tomee-plume-8.0.12\\webapps\\images\\1666885410988OIP.jpg");
        new SearchService().searchByImage(file);

    }

    public List<RecipeVM> searchByResult(Set<String> words) {
        try (Session session = driver.session()) {
            List<RecipeVM> listRecipes = new ArrayList<>();
            for (String string : words) {
                string = string.replace("\n", "").trim();
                for (RecipeVM recipeVM : this.searchByWord(string, session)) {
                    if (!listRecipes.contains(recipeVM)) {
                        System.out.println("###### " + recipeVM.getDate());
                        listRecipes.add(recipeVM);
                    }
                }
            }
            session.close();

            return listRecipes;
        }

    }

    public List<RecipeVM> searchByWord(String word, Session session) {
        String query = "MATCH (recipe:Recipe)WHERE recipe.title  =~ '.*(?i)" + word
                + ".*' OR recipe.type  =~ '.*(?i)"
                + word + ".*' OR recipe.date  =~ '.*(?i)"
                + word + ".*' RETURN recipe.id,recipe.title,recipe.type,recipe.url,recipe.date,recipe.like";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(query);
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
        return listRecipes;

    }

    @Override
    public void close() throws Exception {
        driver.close();

    }

}
