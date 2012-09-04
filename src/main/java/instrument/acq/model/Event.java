package instrument.acq.model;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Event {

	String event;
	String param;
	
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	@Override
	public String toString() {
		return "event: " + event + ", param: " + param;
	}
	
	
	
}
