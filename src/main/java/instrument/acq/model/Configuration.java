package instrument.acq.model;

import instrument.acq.amqp.MessageGetter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration {
	private static final Log log = LogFactory.getLog(MessageGetter.class);

	private Map<String, String> configurationValues;
	
	public Configuration() {
		resetToFactoryDefaults();
	}
	
	public void resetToFactoryDefaults() {
		configurationValues = new HashMap<String,String>();
		configurationValues.put("color", "blue");
		configurationValues.put("temperature", "10C");
	}

	public Map<String, String> getConfigurationValues() {
		return configurationValues;
	}

	public void setConfigurationValues(Map<String, String> configurationValues) {
		this.configurationValues = configurationValues;
	}
	
	public void modifyConfiguration(Map<String, String> newValues) {
		log.debug("Modifying configuration with: " + newValues);
		configurationValues.putAll(newValues);
	}
	
	public void modifyConfiguration(String key, String value) {
		configurationValues.put(key, value);
	}

	@Override
	public String toString() {
		return "Configuration [configurationValues=" + configurationValues
				+ "]";
	}
	
}
