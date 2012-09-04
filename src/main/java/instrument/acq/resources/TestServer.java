package instrument.acq.resources;

import instrument.acq.amqp.TopicReader;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.ext.jaxrs.JaxRsApplication;

/**
 * Start the qpid server before running this.
 * Execute ${QPID_HOME}\bin\qpid_server.bat
 * 
 * @author readym
 *
 */
public class TestServer {

	public static String IM_INPUT_QUEUE = "ImInputQueue:message_queue; {create: always}";

	private static TopicReader imTopicReader;

	public static void main(String[] args) throws Exception {
		// create Component (as ever for Restlet)
		final Component comp = new Component();
		comp.getServers().add(Protocol.HTTP, 80);

		// create JAX-RS runtime environment
		final JaxRsApplication application = new JaxRsApplication(comp
				.getContext().createChildContext());

		// attach ApplicationConfig
		application.add(new TestApplication());

		// Attach the application to the component and start it
		comp.getDefaultHost().attach(application);
		comp.start();

		// start the queue listener to listen for events
		imTopicReader = new TopicReader(IM_INPUT_QUEUE);
		imTopicReader.start();

	}
}
