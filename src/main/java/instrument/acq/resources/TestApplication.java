package instrument.acq.resources;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class TestApplication extends Application {

	 /**
     * creates a new Application configuration for this example.
     */
    public TestApplication() {
    }

    /**
     * @see javax.ws.rs.core.ApplicationConfig#getResourceClasses()
     */
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> rrcs = new HashSet<Class<?>>();
        rrcs.add(AcqResource.class);
        return rrcs;
    }
}
