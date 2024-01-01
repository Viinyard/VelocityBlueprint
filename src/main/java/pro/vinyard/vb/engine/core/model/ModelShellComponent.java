package pro.vinyard.vb.engine.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;
import pro.vinyard.vb.engine.custom.NamedRunnable;
import pro.vinyard.vb.engine.shell.CustomAbstractShellComponent;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@ShellComponent
public class ModelShellComponent extends CustomAbstractShellComponent {

    Logger logger = LoggerFactory.getLogger(ModelShellComponent.class);

    @Autowired
    private ModelManager modelManager;

    @Autowired
    private EnvironmentManager environmentManager;

    public void createModel() {
        String name = stringInput("Enter model name", null, false);

        if (name == null) {
            throw new IllegalArgumentException("Model name cannot be null");
        }

        this.modelManager.createModel(name);
    }

    public void deleteModels() {
        //String name = stringInput("Enter model name", null, false);
        List<SelectorItem<String>> items = this.modelManager.findAllModels().stream().map(m -> SelectorItem.of(m, m)).collect(Collectors.toList());

        List<String> models = multiSelector(items, "Select models to delete");

        if (models == null || models.isEmpty()) {
            throw new RuntimeException("No model selected.");
        }

        for (String model : models) {
            this.modelManager.deleteModel(model);
        }
    }

    public void checkModels() {
        List<SelectorItem<String>> items = this.modelManager.findAllModels().stream().map(m -> SelectorItem.of(m, m)).collect(Collectors.toList());

        List<String> models = multiSelector(items, "Select models to check");

        if (models == null || models.isEmpty()) {
            throw new RuntimeException("No model selected.");
        }

        for (String model : models) {
            try {
                this.modelManager.checkModel(model);
            } catch (VelocityBlueprintException e) {
                logger.error("Model {} is invalid : {}", model, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void listModels() {
        String msg = this.modelManager.listModels();
        PrintWriter printWriter = this.getTerminal().writer();
        printWriter.print(msg);
        printWriter.flush();
    }


    @ShellMethod(key = "model", value = "Action on model", group = "Model")
    @ShellMethodAvailability("environmentAvailability")
    public void model() {
        BiFunction<String, Runnable, SelectorItem<Runnable>> selectorItem = (key, value) -> SelectorItem.of(key, new NamedRunnable(key, value));

        List<SelectorItem<Runnable>> items = Arrays.asList(
                selectorItem.apply("Create model", this::createModel),
                selectorItem.apply("Delete model", this::deleteModels),
                selectorItem.apply("List models", this::listModels),
                selectorItem.apply("Check models", this::checkModels)
        );

        singleSelect(items, "Select action").run();
    }

    public Availability environmentAvailability() {
        return environmentManager.checkEnvironmentInitialized() ? Availability.available() : Availability.unavailable("Environment not initialized.");
    }
}
