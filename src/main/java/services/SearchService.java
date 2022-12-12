package services;

import java.io.File;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import models.RecipeVM;

@Local(ISearchService.class)
@Stateless
public class SearchService implements ISearchService, AutoCloseable {
    private final Driver driver;

    public SearchService() {
        driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "1234"));

    }

    @Override
    public List<RecipeVM> searchByWord(String words) {
        return null;
    }

    @Override
    public List<RecipeVM> searchByImage(File image) {
        return null;
    }

    @Override
    public void close() throws Exception {
        driver.close();

    }

}
