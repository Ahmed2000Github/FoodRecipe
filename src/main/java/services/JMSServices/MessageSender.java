package services.JMSServices;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import models.FeedbackMessage;
import models.User;

@Local(IMessageSender.class)
@Stateless
public class MessageSender implements IMessageSender {
        private static String url = ActiveMQConnectionFactory.DEFAULT_BROKER_URL;

        @Override
        public void sendNewLike(String userId, String recipeName, String senderName) throws JMSException {

                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
                factory.setTrustAllPackages(true);
                Connection connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false,
                                Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(userId);
                MessageProducer producer = session.createProducer(destination);
                String messageId = UUID.randomUUID().toString() + "-message";
                CustomMessage customMessage = new CustomMessage(messageId,
                                userId, senderName,
                                "you have new like added by " + senderName.toUpperCase() + " for the recipe "
                                                + recipeName.toUpperCase(),
                                DateFormat.getInstance().format(new Date()));
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(customMessage);
                producer.send(objectMessage);
                connection.close();
        }

        @Override
        public void sendFeedbacks(User user, String message) throws JMSException {

                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
                factory.setTrustAllPackages(true);
                Connection connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false,
                                Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue("feedbacks");
                MessageProducer producer = session.createProducer(destination);
                String feedbackId = UUID.randomUUID().toString() + "-feedback";
                FeedbackMessage feedbackMessage = new FeedbackMessage(
                                feedbackId, user.getId(),
                                user.getName(), user.getEmail(), DateFormat.getInstance().format(new Date()), message);
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(feedbackMessage);
                producer.send(objectMessage);
                connection.close();
        }

}
