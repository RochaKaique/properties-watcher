package br.com.kaiquera.DemoPropertiesWatcher.watcher;

import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
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

    @Bean
    public FileSystemWatcher watcher() {
        FileSystemWatcher fw = new FileSystemWatcher(true, Duration.ofSeconds(5L), Duration.ofSeconds(1L));
        fw.addSourceDirectory(new File(Paths.get(CONFIG_FILE_OUTDOOR).getParent().toString()));
        fw.addListener(new Listener(environment));
        fw.start();
        return fw;
    }


    private void addInitialPropertySource() throws IOException {
        FileSystemResource resource = new FileSystemResource(CONFIG_FILE_OUTDOOR);
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);

        environment.getPropertySources().addLast(
                new PropertiesPropertySource(PROPERTY_SOURCE_NAME, properties)
        );
        System.out.println("Propriedades iniciais carregadas, valor do conf.teste: " + environment.getProperty("config.teste"));
    }


}
