package pro.vinyard.vb.engine.core.pluginManager;

import org.apache.velocity.VelocityContext;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.plugin.api.VelocityBlueprintExtension;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VbPluginManager {

    @Autowired
    private EnvironmentManager environmentManager;
    Logger logger = LoggerFactory.getLogger(VbPluginManager.class);

    public org.pf4j.PluginManager initPlugins(VelocityContext velocityContext) {
        PluginManager pluginManager = new PluginManager(environmentManager.getPluginDirectory().toPath());

        pluginManager.loadPlugins();
        pluginManager.startPlugins();

        logger.info("Plugin started : {}", pluginManager.getStartedPlugins().stream().map(PluginWrapper::getPluginId).collect(Collectors.joining(", ")));

        List<VelocityBlueprintExtension> plugins = pluginManager.getExtensions(VelocityBlueprintExtension.class);

        logger.info("Extensions found : {}", plugins.stream().map(p -> p.getClass().getName()).collect(Collectors.joining(", ")));

        plugins.forEach(p -> p.init(velocityContext));

        return pluginManager;
    }

    public void unloadPlugins(org.pf4j.PluginManager pluginManager) {
        pluginManager.stopPlugins();
        pluginManager.unloadPlugins();
        logger.info("Stopped plugins.");
    }
}
