package services.JMSServices;

import java.io.Serializable;

// import lombok.AllArgsConstructor;
// import lombok.Data;

// @Data
// @AllArgsConstructor
public class CustomMessage implements Serializable {
    private static final long serialVersionUID = 473126620146399681L;
    private String id;
    private String userId;
    private String senderName;
    private String content;
    private String date;

    public CustomMessage(String id, String userId, String senderName, String content, String date) {
        this.id = id;
        this.userId = userId;
        this.senderName = senderName;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
