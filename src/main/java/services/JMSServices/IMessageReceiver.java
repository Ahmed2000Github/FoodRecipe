package services.JMSServices;

import java.util.List;

import javax.jms.JMSException;

import models.FeedbackMessage;

public interface IMessageReceiver {
    public List<CustomMessage> receiveNewLike(String userId) throws JMSException;

    public List<FeedbackMessage> receiveFeedback() throws JMSException;
}
