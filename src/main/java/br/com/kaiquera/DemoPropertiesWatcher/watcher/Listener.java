package br.com.kaiquera.DemoPropertiesWatcher.watcher;

import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

@Component
public class Listener implements FileChangeListener {
    private static final String CONFIG_FILE_OUTDOOR = "./config/conf.properties";
    private final ConfigurableEnvironment environment;

    public Listener(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void onChange(Set<ChangedFiles> changeSet) {
        try {
            reloadProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
