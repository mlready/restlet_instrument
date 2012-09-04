package instrument.acq.amqp;

import instrument.acq.model.Event;
import instrument.acq.model.Instrument;

import java.net.URISyntaxException;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnection;

/**
 * A thread that listens for message on the specified queue and processes them
 * 
 * @author readym
 * 
 */
public class TopicReader implements Runnable {

	private static final Log log = LogFactory.getLog(TopicReader.class);
	public static String QPID_SERVER = "amqp://guest:guest@test/?brokerlist='tcp://localhost:5672'";

	boolean stop = false;
	boolean isRunning = false;
	String addr;
	long timeout = 30000;

	public TopicReader(String queueAddress) {
		this.addr = queueAddress;
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	public void stop() {
		stop = true;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void run() {
		log.debug("TopicReader --- run ----");
		isRunning = true;
		stop = false;

		Connection connection;
		try {
			// start the qpid connection
			connection = new AMQConnection(QPID_SERVER);
			connection.start();

			// get the queue that has been passed to us
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination queue = new AMQAnyDestination(addr);
			MessageConsumer consumer = session.createConsumer(queue);

			// listen and wait for messages to come into the queue and then
			// process them
			while (!stop) {
				Message message = consumer.receive(timeout);

				if (message != null && message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					log.debug("TextMessage rx:" + text);

					String source = message.getStringProperty("source");
					String command = message.getStringProperty("event");

					Event instrumentEvent = new Event();
					instrumentEvent.setEvent(command);
					instrumentEvent.setParam(text);

					// fire the event for the instrument
					if (source.equals(MessageSender.MESSAGE_SOURCE)) {
						Instrument.getInstance().processEvent(instrumentEvent);
					}

				}

				if (message != null && message instanceof MapMessage) {

					MapMessage mapMessage = (MapMessage) message;

					String source = message.getStringProperty("source");
					String command = message.getStringProperty("event");

					if (source.equals(MessageSender.MESSAGE_SOURCE)) {
						if (command.equals("runConfiguration")) {

							@SuppressWarnings("unchecked")
							Map<String, String> configurationValues = (Map<String, String>) mapMessage
									.getObject("values");
							Instrument.getInstance().getConfig()
									.modifyConfiguration(configurationValues);
						}
					}
				}

			}

			connection.close();

		} catch (URISyntaxException e) {
			log.error(e);

		} catch (AMQException e) {
			log.error(e);

		} catch (JMSException e) {
			log.error(e);
		}

		isRunning = false;

		log.debug(" exit run");

	}

}
