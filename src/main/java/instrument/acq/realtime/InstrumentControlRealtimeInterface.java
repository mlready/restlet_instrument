package instrument.acq.realtime;

/**
 * realtime process adapters call this with "events".
 * 
 * @author mready
 *
 */
public interface InstrumentControlRealtimeInterface {
	
	public Object setFluidicsStatus(Object status);
	
	public Object setChipStatus(Object status);
	
	public Object setSensorStatus(Object status);
	
	public Object setScriptStatus(Object status);
	
	public Object setRunProgress(Object rp);

}
