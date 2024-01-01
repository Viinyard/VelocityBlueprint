package pro.vinyard.vb.engine.core.environment;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.PathInput;
import org.springframework.shell.component.PathSearch;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import pro.vinyard.vb.engine.custom.DirectoryInput;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ShellComponent
public class EnvironnementShellComponent extends AbstractShellComponent {

    private static final String PROPERTY_SEPARATOR = " : ";
    @Autowired
    private EnvironmentManager environmentManager;

    @ShellMethod(key = "environnement", value = "Display environnement", group = "EnvironnementManager")
    public String displayEnvironnement() {
        StringBuilder stringBuilder = new StringBuilder();
        BiConsumer<String, String> appendLine = (key, value) -> appendLine(stringBuilder, key, value);
        Consumer<String> appendCategory = (value) -> appendCategory(stringBuilder, value);
        appendCategory.accept("User environnement");
        appendLine.accept("Home directory", environmentManager.getHomeDirectoryPath());
        appendLine.accept("Working directory", environmentManager.getWorkingDirectory());
        appendCategory.accept("Java environnement");
        appendLine.accept("Java version", environmentManager.getJavaVersion());
        appendLine.accept("Jar File", environmentManager.getJarFileLocation());
        appendLine.accept("Jar directory", environmentManager.getJarDirectoryLocation());
        appendCategory.accept("System environnement");
        appendLine.accept("Operating system", environmentManager.getOperatingSystem());
        appendCategory.accept("");
        return stringBuilder.toString();
    }

    @ShellMethod(key = "home", value = "Set home directory", group = "EnvironnementManager")
    public void setHomeDirectory() {
        DirectoryInput component = new DirectoryInput(getTerminal(), "Enter home directory");
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        PathInput.PathInputContext context = component.run(PathSearch.PathSearchContext.empty());

        environmentManager.setHomeDirectory(context.getResultValue());
    }


    @ShellMethod(key = "init", value = "Initialize environnement", group = "EnvironnementManager")
    @ShellMethodAvailability("environmentAvailability")
    public void init() {
        environmentManager.initialize();
    }

    public Availability environmentAvailability() {
        return environmentManager.checkHomeDirectory() ? Availability.available() : Availability.unavailable("Home directory is not set");
    }

    private void appendCategory(StringBuilder stringBuilder, String category) {
        stringBuilder.append(StringUtils.center(category, 100 + PROPERTY_SEPARATOR.length(), "-"));
        stringBuilder.append(environmentManager.getLineSeparator());
    }

    private void appendLine(StringBuilder stringBuilder, String key, String value) {
        stringBuilder.append(String.format("%1$-20s%2$s%3$80s", key, PROPERTY_SEPARATOR, Optional.ofNullable(value).orElse("(undefined)")));
        stringBuilder.append(environmentManager.getLineSeparator());
    }
}
