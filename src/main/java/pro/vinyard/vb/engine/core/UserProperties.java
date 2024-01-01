package pro.vinyard.vb.engine.core;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import pro.vinyard.vb.engine.core.environment.EnvironmentManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

@Component
public class UserProperties {
    public static final String HOME_DIRECTORY = "home-directory";
    private final ApplicationHome home = new ApplicationHome(getClass());

    private final Properties properties = new Properties();
    Logger log = LoggerFactory.getLogger(EnvironmentManager.class);

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    @PostConstruct
    public void init() {
        File file = new File(home.getDir(), "application.xml");

        if (!file.exists())
            return;

        try (InputStream inputStream = new FileInputStream(file)) {
            this.properties.loadFromXML(inputStream);
        } catch (Exception e) {
            log.warn("Cannot load external properties file.", e);
        }
    }

    @PreDestroy
    public void destroy() {
        File file = new File(home.getDir(), "application.xml");

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.storeToXML(fileOutputStream, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
