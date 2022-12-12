package models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackMessage implements Serializable {
    private static final long serialVersionUID = 473126620146399682L;
    private String id;
    private String userId;
    private String userName;
    private String email;
    private String date;
    private String message;
}
