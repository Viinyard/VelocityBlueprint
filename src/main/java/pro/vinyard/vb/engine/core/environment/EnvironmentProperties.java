package pro.vinyard.vb.engine.core.environment;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "velocity-blueprint")
@Validated
@Data
public class EnvironmentProperties {

    private final Environment environment;

    @ConstructorBinding
    public EnvironmentProperties(Environment environment) {
        this.environment = environment;
    }

    @Data
    public static class Environment {

        private final String workspaceDirectory;

        private final String modelDirectory;

        private final String templateDirectory;

        @ConstructorBinding
        public Environment(String workspaceDirectory, String modelDirectory, String templateDirectory) {
            this.workspaceDirectory = workspaceDirectory;
            this.modelDirectory = modelDirectory;
            this.templateDirectory = templateDirectory;
        }
    }

}
