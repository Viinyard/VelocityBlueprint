package pro.vinyard.vb.engine.core.generation;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class GenerationManager {

    Logger logger = LoggerFactory.getLogger(GenerationManager.class);

    public void processTemplate(VelocityContext velocityContext, File template, File file) {
        Velocity.init();

        try {
            FileUtils.forceMkdirParent(file);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create parent directory.", e);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            Velocity.evaluate(velocityContext, fileWriter, "LOG", new FileReader(template));
            logger.info("File {} generated.", file.getAbsolutePath());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot process template.", e);
        }
    }

    public String processTemplate(VelocityContext velocityContext, File template) throws IOException {
        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "LOG", new FileReader(template));
        return stringWriter.toString();
    }

    public String processTemplate(VelocityContext velocityContext, String template) {
        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "LOG", new StringReader(template));
        return stringWriter.toString();
    }
}
