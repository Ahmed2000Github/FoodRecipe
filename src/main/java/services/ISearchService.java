package services;

import java.io.File;
import java.util.List;
import java.util.Map;

import models.Pair;
import models.RecipeVM;

public interface ISearchService {

    public List<RecipeVM> searchByWord(String words);

    public Pair<List<RecipeVM>, Map<String, Double>> searchByImage(File image);

}
