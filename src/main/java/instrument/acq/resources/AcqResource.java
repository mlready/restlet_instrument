package instrument.acq.resources;

import instrument.acq.amqp.MessageGetter;
import instrument.acq.amqp.MessageSender;
import instrument.acq.model.Instrument;
import instrument.acq.model.InstrumentState;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The main class using jaxrs for REST calls
 * @author readym
 *
 */

@Path("/acq")
public class AcqResource {

	private static final Log log = LogFactory.getLog(AcqResource.class);

	/**
	 * Puts a message on a test queue and then retrieves that message This is
	 * just so be sure qpid server is responding
	 * 
	 * This process requires the qpid server to be up and running Need to go to
	 * ${QPID_HOME}\bin\qpid_server.bat
	 * 
	 * @return
	 */
	@GET
	@Path("/amqp")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAmqp() {

		log.debug("getAmqp");

		try {
			MessageSender.sendTextMessage(TestServer.IM_INPUT_QUEUE,
					"getStatus", "The machine is started");
		} catch (Exception e) {
			log.error(e);
		}

		Object resp;
		try {
			resp = MessageGetter.getMessage(5000, TestServer.IM_INPUT_QUEUE);

			log.debug("resp:" + resp);

			return resp.toString();

		} catch (Exception e) {
			log.error(e);
		}

		log.debug("no resp:");

		return "No messages on the queue";
	}

	/**
	 * Gets the last message from the instrument queue
	 * 
	 * @return
	 */
	@GET
	@Path("/queue")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAmqpText() {

		log.debug("getAmqpText ----- queue:");

		String response = null;
		try {
			response = (String) MessageGetter.getMessage(5000,
					TestServer.IM_INPUT_QUEUE);
		} catch (Exception e) {
			log.error(e);
		}

		return response;
	}

	/**
	 * Returns the configuration of the instrument
	 * 
	 * @return
	 */
	@GET
	@Path("/config")
	@Produces(MediaType.TEXT_PLAIN)
	public String getConfig() {

		log.debug("----- getConfig:");

		StringBuilder sb = new StringBuilder();

		sb.append("im version:xyz \n");
		sb.append("hostname:isfetxyz \n");
		sb.append("config:" + Instrument.getInstance().getConfig());

		return sb.toString();
	}

	/**
	 * Gets the state directly from the instrument
	 * 
	 * @return
	 */
	@GET
	@Path("/get/state")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public String getInstrumentState() {
		log.debug("get-state-app-xml");

		InstrumentState is = Instrument.getInstance().getState();
		log.debug("Current state: " + is.getState());
		return is.getState();
	}

	/**
	 * Right now just returns a string
	 * 
	 * @return
	 */
	@GET
	@Path("/run/info")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRunInfo() {

		log.debug("IM ----- getRunInfo:");

		StringBuilder sb = new StringBuilder();

		sb.append("Current State: " + Instrument.getInstance().getState().getState() + " ");
		sb.append("Start time: " + new Date(Instrument.getInstance().getState().getStartTime()).toString() + " ");
		sb.append("Run History: " + Instrument.getInstance().getRunProgress().toString());

		return sb.toString();
	}

	/**
	 * Puts a message on the queue to update the configuration with a specific
	 * parameter
	 * 
	 * @param event
	 * @param param
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/run/config")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response postRunConfig(@FormParam("config") String config,
			@FormParam("param") String param) throws IOException {
		log.debug("postRunConfig p:" + param);

		// queue this request
		try {
			Map<String,String> values = new HashMap<String,String>();
			values.put(config, param);
			
			MessageSender.sendMapMessage(TestServer.IM_INPUT_QUEUE, "runConfiguration",values);
		} catch (Exception e) {
			log.error(e);
		}

		return Response.noContent().build();
	}

	/**
	 * Puts a message on the queue to start the instrument
	 * 
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/run/start")
	public Response postRunStart() throws IOException {
		log.debug("postRunStart - text");

		// send to InstrumentManager
		try {
			MessageSender.sendTextMessage(TestServer.IM_INPUT_QUEUE, "Start",
					"Starting the instrument");
		} catch (Exception e) {
			log.error(e);
		}

		return Response.noContent().build();
	}

	/**
	 * Sets the instrument state to something else
	 * 
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/set/state")
	public Response postSetState(@FormParam("event") String event) throws IOException {
		log.debug("postSetState ");

		// queue message
		try {
			MessageSender.sendTextMessage(TestServer.IM_INPUT_QUEUE, event, "");
		} catch (Exception e) {
			log.error(e);
		}

		return Response.noContent().build();
	}

}
