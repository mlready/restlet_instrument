package instrument.acq;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.env.SimpleDispatcher;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.env.SimpleErrorReporter;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.xml.sax.ErrorHandler;

/**
 * Defines the instrument states using Apache SCXML
 * 
 * @author readym
 * 
 */
public class InstrumentStateMachine {

	private static final Log log = LogFactory.getLog(InstrumentStateMachine.class);
	public static final String SCXML_FILE = "insDm.scxml";

	private SCXML stateMachine;
	private SCXMLExecutor engine;
	private boolean started = false;


	/**
	 * State machine is initialized here using location of scxml file
	 * 
	 * @param scxml
	 */
	public InstrumentStateMachine(URL scxml) {
		Context rootCtx = new JexlContext();
		Evaluator evaluator = new JexlEvaluator();
		ErrorHandler errHandler = new SimpleErrorHandler();

		// initialize the state machine
		try {
			stateMachine = SCXMLParser.parse(scxml, errHandler);
			initialize(stateMachine, rootCtx, evaluator);
		} catch (Exception ex) {
			log.error("Failed to initialize state machine", ex);
		}
	}


	public void addListener(SCXMLListener l) {
		engine.addListener(this.stateMachine, l);
	}

	/**
	 * Starts the state machine if it is not already started
	 */
	public void go() {
		if (started) {
			log.debug("already started");
			return;
		}
		log.debug("go");
		try {
			started = true;
			engine.go();
		} catch (ModelException me) {
			log.error("Failed to start state machine", me);
		}
	}

	/**
	 * Convenience method for knowing if the engine as started
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Returns the scxml executor for this instrument
	 * 
	 * @return
	 */
	public SCXMLExecutor getSM() {
		return this.engine;
	}

	/**
	 * Test runner for state machine
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		URL url = InstrumentStateMachine.class.getClassLoader().getResource(
				SCXML_FILE);
		log.debug("url:" + url);
		 
		InstrumentStateMachine sm = new InstrumentStateMachine(url);
		sm.go();
	}

	private void initialize(final SCXML sm, final Context rootCtx,
			final Evaluator eval) {
		engine = new SCXMLExecutor(eval, new SimpleDispatcher(),
				new SimpleErrorReporter());
		engine.setStateMachine(sm);
		engine.setSuperStep(true);
		engine.setRootContext(rootCtx);

	}

}
