package nb.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;


@ApplicationPath("/")
public class NirmalBharat extends Application {
	
	    @Override
	    public Set<Class<?>> getClasses() {
	        final Set<Class<?>> classes = new HashSet<Class<?>>();
	        // register resources and features
	        classes.add(MultiPartFeature.class);
	        classes.add(CleanInfo.class);	       
	        return classes;
	    }
}

