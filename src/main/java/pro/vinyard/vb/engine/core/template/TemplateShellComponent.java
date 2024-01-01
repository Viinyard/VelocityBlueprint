package pro.vinyard.vb.engine.core.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import pro.vinyard.vb.engine.custom.NamedRunnable;
import pro.vinyard.vb.engine.shell.CustomAbstractShellComponent;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.BiFunction;

@ShellComponent
public class TemplateShellComponent extends CustomAbstractShellComponent {

    @Autowired
    private TemplateManager templateManager;

    @ShellMethod(key = "template", value = "Action on template", group = "Template")
    public void template() {
        BiFunction<String, Runnable, SelectorItem<Runnable>> selectorItem = (key, value) -> SelectorItem.of(key, new NamedRunnable(key, value));

        List<SelectorItem<Runnable>> items = List.of(
                selectorItem.apply("Create template", this::createTemplate),
                selectorItem.apply("Delete template", this::deleteTemplates),
                selectorItem.apply("List templates", this::listTemplates)
        );

        singleSelect(items, "Select action").run();
    }

    public void createTemplate() {
        String name = stringInput("Enter template name", null, false);

        if (name == null) {
            throw new IllegalArgumentException("Template name cannot be null");
        }

        this.templateManager.createTemplate(name);
    }

    public void deleteTemplates() {
        List<SelectorItem<String>> items = this.templateManager.findAllTemplates().stream().map(File::getName).map(m -> SelectorItem.of(m, m)).toList();

        List<String> templates = multiSelector(items, "Select templates to delete");

        if (templates == null || templates.isEmpty()) {
            throw new RuntimeException("No template selected.");
        }

        for (String template : templates) {
            this.templateManager.deleteTemplate(template);
        }
    }

    public void listTemplates() {
        PrintWriter writer = getTerminal().writer();
        String msg = this.templateManager.listTemplates();
        writer.print(msg);
        writer.flush();
    }
}
