package pro.vinyard.vb.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pro.vinyard.vb.engine.core.environment.EnvironmentProperties;

@SpringBootApplication
@EnableConfigurationProperties(EnvironmentProperties.class)
public class VbEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(VbEngineApplication.class);
    }

}
