package services.JMSServices;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.MessageConsumer;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import models.FeedbackMessage;

@Local(IMessageReceiver.class)
@Stateful
public class MessageReceiver implements IMessageReceiver {
    private static String url = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;

    @Override

    public List<CustomMessage> receiveNewLike(String userId) throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        factory.setTrustAllPackages(true);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        System.out.println("&&&&&&&&&&&&&&&&&&& " + userId);
        Destination destination = session.createQueue(userId);
        MessageConsumer consumer = session.createConsumer(destination);
        // consumer.setMessageListener(new CustomMessageListener());
        boolean hasNextMessage = true;
        List<CustomMessage> list = new ArrayList<>();
        while (hasNextMessage) {

            Message message = consumer.receive(1000);
            System.out.println("############### " + message + " #########");
            if (message != null) {
                Object object = ((ObjectMessage) message).getObject();
                CustomMessage customMessage = (CustomMessage) object;
                if (!list.contains(customMessage)) {
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +
                            customMessage.getContent());
                    list.add(customMessage);
                }
            } else {
                hasNextMessage = false;
            }
        }
        connection.close();

        return list;
    }

    @Override
    public List<FeedbackMessage> receiveFeedback() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        factory.setTrustAllPackages(true);
        Connection connection = factory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("feedbacks");
        MessageConsumer consumer = session.createConsumer(destination);
        // consumer.setMessageListener(new CustomMessageListener());
        boolean hasNextMessage = true;
        List<FeedbackMessage> list = new ArrayList<>();
        while (hasNextMessage) {

            Message message = consumer.receive(1000);
            System.out.println("############### " + message + " #########");
            if (message != null) {
                Object object = ((ObjectMessage) message).getObject();
                FeedbackMessage feedbackMessage = (FeedbackMessage) object;
                if (!list.contains(feedbackMessage)) {
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +
                            feedbackMessage.getMessage());
                    list.add(feedbackMessage);
                }
            } else {
                hasNextMessage = false;
            }
        }
        connection.close();

        return list;
    }

    // public static void main(String[] args) throws JMSException {
    // String userId = "63ec5ddf-6497-4380-be45-979c1ewb4bea-user";
    // ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
    // factory.setTrustAllPackages(true);
    // Connection connection = factory.createConnection();
    // connection.start();

    // Session session = connection.createSession(false,
    // Session.AUTO_ACKNOWLEDGE);
    // System.out.println("&&&&&&&&&&&&&&&&&&& " + userId);
    // Destination destination =
    // session.createQueue("63ec5ddf-6497-4380-be45-979c1ewb4bea-user");
    // MessageConsumer consumer = session.createConsumer(destination);
    // // consumer.setMessageListener(new CustomMessageListener());
    // boolean hasNextMessage = true;
    // List<CustomMessage> list = new ArrayList<>();
    // while (hasNextMessage) {

    // Message message = consumer.receive(1000);
    // System.out.println("############### " + message + " #########");
    // if (message != null) {
    // Object object = ((ObjectMessage) message).getObject();
    // CustomMessage customMessage = (CustomMessage) object;
    // if (!list.contains(customMessage)) {
    // System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +
    // customMessage.getContent());
    // list.add(customMessage);
    // }
    // } else {
    // hasNextMessage = false;
    // }
    // }
    // connection.close();
    // }

}