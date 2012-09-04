package instrument.acq.model;

import instrument.acq.InstrumentControlUserInterface;
import instrument.acq.InstrumentStateMachine;
import instrument.acq.realtime.InstrumentControlRealtimeInterface;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml.Status;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.State;

/**
 * Contains state of the instrument throughout processing.
 * 
 * @author readym
 * 
 */
public class Instrument implements InstrumentControlRealtimeInterface,
		InstrumentControlUserInterface {
	private static final Log log = LogFactory.getLog(Instrument.class);

	static Instrument instance;

	private InstrumentStateMachine stateMachine;

	private Object status;
	private Configuration config = new Configuration();
	private Object scriptStatus;
	private Object chipStatus;
	private Object sensorStatus;
	private Object fluidicsStatus;
	private Configuration runConfig;
	private List<String> runProgress;

	static {
		instance = new Instrument();
	}

	public static Instrument getInstance() {
		return instance;
	}

	private Instrument() {
		// location of the state definition file
		String scxml = InstrumentStateMachine.SCXML_FILE;
		URL url = Instrument.class.getClassLoader().getResource(scxml);

		// keeps track of states run
		runProgress = new ArrayList<String>();

		// set up default configuration
		runConfig = new Configuration();

		log.debug("Instrument scxml url:" + url);

		// start the state machine
		InstrumentStateMachine sm = new InstrumentStateMachine(url);
		this.stateMachine = sm;

		this.start();
	}

	public InstrumentState getState() {
		Status status = this.stateMachine.getSM().getCurrentStatus();

		@SuppressWarnings("unchecked")
		Set<State> states = status.getStates();
		List<State> sl = new ArrayList<State>(states);

		State state = (State) sl.get(0);

		InstrumentState is = new InstrumentState();
		is.setStartTime(System.currentTimeMillis());
		is.setState(state.getId());
		is.setDetails(state.getHistory().toString());

		return is;
	}

	public void processEvent(Event e) {
		log.debug("Instrument - processEvent e:" + e.getEvent() + " p:"
				+ e.getParam());
		fireEvent(e.getEvent(), e.getParam());
	}

	private void fireEvent(String event, String arg) {
		log.debug("Instrument - fireEvent e:" + event + " arg:" + arg);
		String name = event;
		int itype = TriggerEvent.SIGNAL_EVENT;

		setRunProgress(event);

		Object payload = new Payload(arg);

		TriggerEvent te = new TriggerEvent(name, itype, payload);
		try {
			stateMachine.getSM().triggerEvent(te);
		} catch (ModelException e) {
			log.error(e);
		}
	}

	private void start() {
		stateMachine.go();
	}

	// used for setting configuration values
	public class Payload {
		private String value;

		public Payload(String s) {
			value = s;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String s) {
			this.value = s;
		}
	}

	@Override
	public Object setFluidicsStatus(Object status) {
		return this.fluidicsStatus;
	}

	@Override
	public Object setChipStatus(Object status) {
		return this.chipStatus;
	}

	@Override
	public Object setSensorStatus(Object status) {
		//
		return this.sensorStatus;
	}

	@Override
	public Object setScriptStatus(Object status) {
		//
		return this.scriptStatus;
	}

	@Override
	public Configuration setConfig(Configuration config) {
		this.config = config;
		return this.config;
	}

	@Override
	public Configuration getConfig() {
		return config;
	}

	@Override
	public Object getStatus() {
		return this.status;
	}

	@Override
	public Configuration setRunConfig(Configuration config) {
		//
		this.runConfig = config;
		return this.runConfig;
	}

	@Override
	public Object startRun() {
		// send event
		Event e = new Event();
		e.setEvent("runCommand");
		e.setParam("");

		// process event
		this.processEvent(e);

		return null;
	}

	@Override
	public Object abortRun() {
		// send event
		Event e = new Event();
		e.setEvent("abortRun");
		e.setParam("");

		// process event
		this.processEvent(e);

		return null;
	}

	@Override
	public Object getRunProgress() {
		return this.runProgress.toString();
	}

	@Override
	public Object setRunProgress(Object rp) {
		this.runProgress.add(rp.toString());
		return rp;
	}

}
