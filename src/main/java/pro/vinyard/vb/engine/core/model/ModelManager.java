package pro.vinyard.vb.engine.core.model;

import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.environment.entities.Environment;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;
import pro.vinyard.vb.engine.core.generation.GenerationManager;
import pro.vinyard.vb.engine.core.model.entities.Directive;
import pro.vinyard.vb.engine.core.model.entities.Directives;
import pro.vinyard.vb.engine.core.model.entities.Model;
import pro.vinyard.vb.engine.core.template.TemplateManager;
import pro.vinyard.vb.engine.core.utils.DirectoryUtils;
import pro.vinyard.vb.engine.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ModelManager {

    Logger logger = LoggerFactory.getLogger(ModelManager.class);

    public static final String FILE_LOCATION = "config/model/";
    public static final String CONFIGURATION_FILE = "model.xml";
    public static final String SCHEMA_FILE = "model.xsd";

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private TemplateManager templateManager;

    @Autowired
    private GenerationManager generationManager;

    public void createModel(String name) {
        File modelFolder = DirectoryUtils.createFolder(environmentManager.getModelDirectory(), name);
        logger.info("Model directory {} created.", modelFolder.getAbsolutePath());

        File xmlFile = FileUtils.copyFile(FILE_LOCATION + CONFIGURATION_FILE, new File(modelFolder, CONFIGURATION_FILE));
        logger.info("Model configuration file {} created.", xmlFile.getAbsolutePath());

        File xsdFile = FileUtils.copyFile(FILE_LOCATION + SCHEMA_FILE, new File(modelFolder, SCHEMA_FILE));
        logger.info("Model configuration validator file {} created.", xsdFile.getAbsolutePath());
    }

    public void deleteModel(String name) {
        File modelFolder = new File(environmentManager.getModelDirectory(), name);

        if (!modelFolder.exists())
            throw new RuntimeException(String.format("Model %s does not exist.", name));

        DirectoryUtils.deleteFolder(modelFolder);

        logger.info("Model {} deleted.", name);
    }

    public String listModels() {
        StringBuilder stringBuilder = new StringBuilder();
        Consumer<String> appendLine = (value) -> stringBuilder.append(value).append(environmentManager.getLineSeparator());

        List<String> models = findAllModels();

        if (models == null || models.isEmpty()) {
            appendLine.accept("No model found.");
            return stringBuilder.toString();
        }

        appendLine.accept("Models:");
        models.forEach((model) -> appendLine.accept(String.format(" - %s", model)));

        return stringBuilder.toString();
    }

    public File getModelFolder(String name) {
        return new File(environmentManager.getModelDirectory(), name);
    }

    public File getConfigurationFile(String name) {
        return new File(getModelFolder(name), CONFIGURATION_FILE);
    }

    public File getSchemaFile(String name) {
        return new File(getModelFolder(name), SCHEMA_FILE);
    }

    public void checkModel(String name) throws VelocityBlueprintException {
        File modelFolder = getModelFolder(name);

        if (!modelFolder.exists()) {
            throw new VelocityBlueprintException(String.format("Model %s does not exist.", name));
        }

        File xmlFile = getConfigurationFile(name);
        if (!xmlFile.exists()) {
            throw new VelocityBlueprintException(String.format("Model configuration file %s does not exist.", xmlFile.getName()));
        }

        File xsdFile = getSchemaFile(name);
        if (!xsdFile.exists()) {
            throw new VelocityBlueprintException(String.format("Model configuration validator file %s does not exist.", xsdFile.getName()));
        }

        this.checkConfigurationFile(xmlFile);

        logger.info("Model {} is valid.", name);
    }

    private void checkConfigurationFile(File xmlFile) throws VelocityBlueprintException {
        Model model = loadConfigurationFile(xmlFile);

        List<String> templates = Optional.of(model).map(Model::getDirectives).map(Directives::getDirectiveList).orElseGet(Collections::emptyList).stream().map(Directive::getTemplate).toList();

        templates.forEach(t -> templateManager.checkTemplate(t));
    }

    public Model loadModel(VelocityContext velocityContext, String name) throws VelocityBlueprintException, IOException {
        File xmlFile = getConfigurationFile(name);
        Environment environment = this.environmentManager.loadConfigurationFile();

        environment.getProperties().getPropertyList().forEach(p -> velocityContext.put(p.getKey(), p.getValue()));

        String configuration = generationManager.processTemplate(velocityContext, xmlFile);

        return loadConfigurationFile(configuration);
    }


    private Model loadConfigurationFile(File xmlFile) throws VelocityBlueprintException {
        return FileUtils.loadConfigurationFile(xmlFile, Model.class, FILE_LOCATION + SCHEMA_FILE);
    }

    private Model loadConfigurationFile(String xml) throws VelocityBlueprintException {
        return FileUtils.loadConfigurationFile(xml, Model.class, FILE_LOCATION + SCHEMA_FILE);
    }

    public List<String> findAllModels() {
        return DirectoryUtils.listFolders(environmentManager.getModelDirectory()).stream().map(File::getName).toList();
    }
}
