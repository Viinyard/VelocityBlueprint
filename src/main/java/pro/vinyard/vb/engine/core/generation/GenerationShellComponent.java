package pro.vinyard.vb.engine.core.generation;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.MathTool;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;
import pro.vinyard.vb.engine.core.exception.VelocityBlueprintException;
import pro.vinyard.vb.engine.core.model.ModelManager;
import pro.vinyard.vb.engine.core.model.entities.*;
import pro.vinyard.vb.engine.core.pluginManager.VbPluginManager;
import pro.vinyard.vb.engine.shell.CustomAbstractShellComponent;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@ShellComponent
public class GenerationShellComponent extends CustomAbstractShellComponent {

    @Autowired
    private ModelManager modelManager;

    @Autowired
    private GenerationManager generationManager;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private VbPluginManager vbPluginManager;

    @ShellMethod(key = "generate", value = "Generate from a model", group = "Generation")
    public void generate() throws VelocityBlueprintException, IOException {
        List<SelectorItem<String>> selectorItemList = this.modelManager.findAllModels().stream().map(m -> SelectorItem.of(m, m)).toList();
        String modelName = this.singleSelect(selectorItemList, "Select models to generate from");

        this.modelManager.checkModel(modelName);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("date", new DateTool());
        velocityContext.put("math", new MathTool());

        Model model = this.modelManager.loadModel(velocityContext, modelName);

        PluginManager pluginManager = vbPluginManager.initPlugins(velocityContext);

        Optional.of(model).map(Model::getProperties).map(Properties::getPropertyList).orElseGet(Collections::emptyList).forEach(p -> velocityContext.put(p.getKey(), p.getValue()));

        Stream.of(
                Optional.of(model).map(Model::getPrompts).map(Prompts::getMultiSelectList).orElseGet(Collections::emptyList),
                Optional.of(model).map(Model::getPrompts).map(Prompts::getMonoSelectList).orElseGet(Collections::emptyList),
                Optional.of(model).map(Model::getPrompts).map(Prompts::getStringInputList).orElseGet(Collections::emptyList)
        ).flatMap(List::stream).map(PromptType.class::cast).sorted().forEach(s ->
                velocityContext.put(s.getValue(), this.prompt(s, velocityContext))
        );

        model.getDirectives().getDirectiveList().forEach(d -> {
            try {
                this.processDirective(velocityContext, d);
            } catch (Exception e) {
                throw new RuntimeException("Cannot process template.", e);
            }
        });

        vbPluginManager.unloadPlugins(pluginManager);
    }

    public void processDirective(VelocityContext velocityContext, Directive directive) {
        Function<String, String> templated = s -> generationManager.processTemplate(velocityContext, s);
        File template = new File(environmentManager.getTemplateDirectory(), Optional.of(directive).map(Directive::getTemplate).map(templated).map(String::trim).orElse(""));
        File file = new File(environmentManager.getHomeDirectory(), Optional.of(directive).map(Directive::getValue).map(templated).map(String::trim).orElse(""));
        this.generationManager.processTemplate(velocityContext, template, file);
    }

    public Object prompt(PromptType promptType, VelocityContext velocityContext) {
        Function<String, String> templated = s -> generationManager.processTemplate(velocityContext, s);
        return switch (promptType) {
            case MultiSelect e ->
                    this.multiSelector(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()), v.isEnabled(), v.isSelected())).toList(), e.getValue());
            case MonoSelect e ->
                    this.singleSelect(e.getValueList().stream().map(v -> SelectorItem.of(templated.apply(v.getKey()), templated.apply(v.getContent()))).toList(), e.getValue());
            case StringInput e ->
                    this.stringInput(templated.apply(e.getValue()), Optional.ofNullable(e.getDefaultValue()).map(DefaultValue::getContent).map(templated).orElse(""), e.isMasked());
            default -> throw new IllegalStateException("Unexpected value: " + promptType);
        };
    }

}
