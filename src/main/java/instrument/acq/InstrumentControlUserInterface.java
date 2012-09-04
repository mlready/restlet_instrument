package instrument.acq;

import instrument.acq.model.Configuration;
import instrument.acq.model.InstrumentState;

/**
 * Contains methods for actions relating to the user
 * 
 * @author readym
 *
 */
public interface InstrumentControlUserInterface 
{
	/**
	 * Sets the configuration for the instrument
	 * 
	 * @param config
	 * @return
	 */
	public Configuration setConfig(Configuration config);
	
	/**
	 * Retrieves the configuration for the instrument
	 * 
	 * @return
	 */
	public Configuration getConfig();
	
	/**
	 * Retrieves the current state of the instrument (i.e., running, ...)
	 * States are defined in the scxml state file
	 * 
	 * @return
	 */
	public InstrumentState getState();
	
	/**
	 * Retrieves the status of the instrument
	 * 
	 * @return
	 */
	public Object getStatus();
	
	/**
	 * Sets the run time configuration (overrides the default configuration)
	 * @param config
	 * @return
	 */
	public Configuration setRunConfig(Configuration config);
	
	/**
	 * Starts a run
	 * 
	 * @return
	 */
	public Object startRun();
	
	/**
	 * Aborts a run in progress
	 * 
	 * @return
	 */
	public Object abortRun();

	/**
	 * Returns the run progress
	 * 
	 * @return
	 */
	public Object getRunProgress();
	
	

}
