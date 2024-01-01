package pro.vinyard.vb.engine.core.template;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

@Component
public class TemplateManager {

    Logger logger = LoggerFactory.getLogger(TemplateManager.class);

    public static final String FILE_LOCATION = "config/template/";
    public static final String TEMPLATE_FILE = "template.vm";

    @Autowired
    private EnvironmentManager environmentManager;

    public List<File> findAllTemplates() {
        return FileUtils.listFiles(environmentManager.getTemplateDirectory(), "vm");
    }

    public void checkTemplate(String name) {
        File templateFile = new File(environmentManager.getTemplateDirectory(), name);

        if (!templateFile.exists())
            throw new IllegalArgumentException(String.format("Template %s does not exist.", name));
    }

    public String listTemplates() {
        StringBuilder stringBuilder = new StringBuilder();
        Consumer<String> appendLine = (value) -> stringBuilder.append(value).append(environmentManager.getLineSeparator());

        List<String> templates = findAllTemplates().stream().map(File::getAbsolutePath).map(s -> StringUtils.removeStart(s, environmentManager.getTemplateDirectory().getAbsolutePath())).map(s -> StringUtils.removeStart(s, "\\")).toList();

        if (templates.isEmpty()) {
            appendLine.accept("No template found.");
            return stringBuilder.toString();
        }

        appendLine.accept("Templates:");
        templates.forEach((t) -> appendLine.accept(String.format(" - %s", t)));

        return stringBuilder.toString();
    }

    public void createTemplate(String name) {
        FileUtils.copyFile(FILE_LOCATION + TEMPLATE_FILE, new File(environmentManager.getTemplateDirectory(), name + ".vm"));
        logger.info("Template {} created.", name + ".vm");
    }

    public void deleteTemplate(String name) {
        File templateFile = new File(environmentManager.getTemplateDirectory(), name);

        if (!templateFile.exists())
            throw new RuntimeException(String.format("Template %s does not exist.", name));

        if (templateFile.delete())
            logger.info("Template {} deleted.", name);
        else
            logger.warn("Template {} not deleted.", name);
    }
}
