package pro.vinyard.vb.engine.shell;

import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.List;
import java.util.Optional;

public class CustomAbstractShellComponent extends AbstractShellComponent {

    public <T> List<T> multiSelector(List<SelectorItem<T>> items, String message) {
        MultiItemSelector<T, SelectorItem<T>> component = new MultiItemSelector<>(getTerminal(), items, message, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        MultiItemSelector.MultiItemSelectorContext<T, SelectorItem<T>> context = component.run(MultiItemSelector.MultiItemSelectorContext.empty());

        return context.getResultItems().stream().map(Itemable::getItem).toList();
    }

    public String stringInput(String message, String defaultValue, boolean mask) {
        StringInput component = new StringInput(getTerminal(), message, defaultValue);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if (mask) {
            component.setMaskCharacter('*');
        }
        StringInput.StringInputContext context = component.run(StringInput.StringInputContext.empty());
        return context.getResultValue();
    }

    public <T> T singleSelect(List<SelectorItem<T>> items, String message) {
        SingleItemSelector<T, SelectorItem<T>> component = new SingleItemSelector<>(getTerminal(), items, message, null);

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<T, SelectorItem<T>> context = component.run(SingleItemSelector.SingleItemSelectorContext.empty());

        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElseThrow();
    }


}
