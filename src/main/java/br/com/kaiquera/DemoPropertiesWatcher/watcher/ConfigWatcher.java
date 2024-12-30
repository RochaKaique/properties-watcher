package br.com.kaiquera.DemoPropertiesWatcher.watcher;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
@EnableScheduling
public class ConfigWatcher {
    private final ConfigurableEnvironment environment;
    private static final String CONFIG_FILE_OUTDOOR = "./config/conf.properties";

    private static final String PROPERTY_SOURCE_NAME = "dynamicProperties";
    private long lastModifiedTime = 0L;

    public ConfigWatcher(ConfigurableEnvironment environment) throws IOException {
        this.environment = environment;
        addInitialPropertySource();
    }

    @Scheduled(fixedRate = 1000)
    public void watchFile() {
        try {
            Path path = Paths.get(CONFIG_FILE_OUTDOOR).getParent();
            if (Files.exists(path)) {
                long currentModifiedTime = Files.getLastModifiedTime(path).toMillis();

                if (currentModifiedTime > lastModifiedTime) {
                    lastModifiedTime = currentModifiedTime;
                    reloadProperties();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addInitialPropertySource() throws IOException {
        FileSystemResource resource = new FileSystemResource(CONFIG_FILE_OUTDOOR);
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);

        environment.getPropertySources().addLast(
                new PropertiesPropertySource(PROPERTY_SOURCE_NAME, properties)
        );
        System.out.println("Propriedades iniciais carregadas, valor do conf.teste: " + environment.getProperty("config.teste"));
    }

    private void reloadProperties() throws IOException {
        FileSystemResource resource = new FileSystemResource(CONFIG_FILE_OUTDOOR);
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        environment.getPropertySources().replace(
                "dynamicProperties",
                new PropertiesPropertySource("dynamicProperties", properties)
        );
        System.out.println("Configurações recarregadas! valor do conf.teste: " + environment.getProperty("config.teste"));
    }
}
