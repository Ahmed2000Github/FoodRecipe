package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecipeVM {

    private String id;
    private String title;
    private String url;
    private String comments;
    private String likes;
    private String date;

}
