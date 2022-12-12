package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserRecipes {
    User user;
    List<RecipeVM> recipes;
    int allLikes;
    int allComments;
    int recipesCount;
}
