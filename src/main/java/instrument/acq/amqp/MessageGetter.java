package instrument.acq.amqp;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;

/**
 * Retrieves messages on a defined message queue
 * Processes the message according to the message type either text or map
 * 
 * @author readym
 *
 */
public class MessageGetter {
	private static final Log log = LogFactory.getLog(MessageGetter.class);
	private static final String QPID_CONNECTION = "amqp://guest:guest@test/?brokerlist='tcp://localhost:5672'";

	@SuppressWarnings("rawtypes")
	public static Object getMessage(long timeout, String destination)
			throws Exception {
		System.out.println("getMessage");

		// set up and start the qpid connection
		Connection connection = new AMQConnection(QPID_CONNECTION);
		connection.start();

		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		Destination queue = new AMQAnyDestination(destination);
		MessageConsumer consumer = session.createConsumer(queue);

		// get a message off the queue
		Message message = consumer.receive(timeout);
		log.debug(message);
		connection.close();

		if (message == null) {
			return "no message on queue";
		}

		if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;
			String text = tm.getText();
			log.debug("TextMessage text:" + text);
			return text;
		}

		if (message instanceof MapMessage) {

			MapMessage mapMessage = (MapMessage) message;
			StringBuffer sb = new StringBuffer();
			Enumeration mapNames = mapMessage.getMapNames();
			int elementCounter = 0;
			while (mapNames.hasMoreElements()) {
				Object mapName = mapNames.nextElement();
				sb.append("mapName:" + elementCounter++ + ":");
				Object mapValue = mapMessage.getObject(mapName.toString());
				sb.append(mapName + ":" + mapValue);
				sb.append("\n");
			}

			Enumeration mapProperties = message.getPropertyNames();
			int propertyCount = 0;
			while (mapProperties.hasMoreElements()) {
				Object mapProperty = mapProperties.nextElement();
				sb.append("prop:" + propertyCount++ + ":");
				Object propertyValue = message.getObjectProperty(mapProperty.toString());
				sb.append(mapProperty + ":" + propertyValue);
				sb.append("\n");
			}

			return sb.toString();
		}

		return "unknown message type:" + message.getClass().getName();
	}
}
