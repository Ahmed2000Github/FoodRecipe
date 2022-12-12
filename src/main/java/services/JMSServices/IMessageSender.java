package services.JMSServices;

import javax.jms.JMSException;

import models.User;

public interface IMessageSender {
    public void sendNewLike(String userId, String recipeName, String senderName) throws JMSException;

    public void sendFeedbacks(User user, String message) throws JMSException;
}
