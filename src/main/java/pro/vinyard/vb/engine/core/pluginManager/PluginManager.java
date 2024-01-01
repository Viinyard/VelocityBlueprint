package pro.vinyard.vb.engine.core.pluginManager;

import org.pf4j.DefaultPluginManager;

import java.nio.file.Path;

public class PluginManager extends DefaultPluginManager {

    public PluginManager(Path... pluginsRoots) {
        super(pluginsRoots);
    }

    @Override
    protected org.pf4j.PluginFactory createPluginFactory() {
        return new PluginFactory();
    }
}
