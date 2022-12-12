package services;

import java.io.File;
import java.util.List;

import models.RecipeVM;

public interface ISearchService {

    public List<RecipeVM> searchByWord(String words);

    public List<RecipeVM> searchByImage(File image);

}
