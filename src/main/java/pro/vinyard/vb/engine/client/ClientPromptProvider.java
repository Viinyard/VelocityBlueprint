package pro.vinyard.vb.engine.client;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class ClientPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("vb>", AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
    }
}
