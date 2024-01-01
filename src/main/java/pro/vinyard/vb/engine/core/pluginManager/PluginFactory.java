package pro.vinyard.vb.engine.core.pluginManager;

import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.vinyard.vb.plugin.api.PluginContext;


public class PluginFactory extends DefaultPluginFactory {

    private static final Logger log = LoggerFactory.getLogger(PluginFactory.class);

    @Override
    public Plugin create(PluginWrapper pluginWrapper) {
        PluginContext context = new PluginContext(pluginWrapper.getRuntimeMode());
        try {
            Class<?> pluginClass = pluginWrapper.getPluginClassLoader().loadClass(pluginWrapper.getDescriptor().getPluginClass());
            return (Plugin) pluginClass.getConstructor(PluginContext.class).newInstance(context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}

