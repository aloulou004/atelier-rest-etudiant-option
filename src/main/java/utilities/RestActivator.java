package utilities;

import ressources.EtudiantRessource;
import ressources.OptionRessource;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("rest")
public class RestActivator extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(OptionRessource.class);
        classes.add(EtudiantRessource.class);
        classes.add(JacksonFeature.class);
        return classes;
    }
}
