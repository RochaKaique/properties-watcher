package br.com.kaiquera.DemoPropertiesWatcher.watcher;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class ConfigWatcher {
    private final ConfigurableEnvironment environment;
    private static final String CONFIG_FILE_OUTDOOR = "./config/conf.properties";

    private static final String PROPERTY_SOURCE_NAME = "dynamicProperties";
    private volatile FileTime currentLastModifiedTime = FileTime.fromMillis(0);

    public ConfigWatcher(ConfigurableEnvironment environment) throws IOException {
        this.environment = environment;
        addInitialPropertySource();
        startPolling();
    }

//    private void initializeWatcher() {
//        try {
//            Path path = Paths.get(CONFIG_FILE_OUTDOOR).getParent();
//            WatchService watchService = FileSystems.getDefault().newWatchService();
//            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//
//            addInitialPropertySource();
//
//            Thread.startVirtualThread(() -> {
//                while (true) {
//                    try {
//                        WatchKey key = watchService.take();
//                        for (WatchEvent<?> event : key.pollEvents()) {
//                                reloadProperties();
//                        }
//                        key.reset();
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        return;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void startPolling() {
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    Path path = Paths.get(CONFIG_FILE_OUTDOOR);

                    // Obtém o tempo da última modificação
                    FileTime lastModifiedTime = Files.getLastModifiedTime(path);

                    // Compara com o valor armazenado
                    if (!lastModifiedTime.equals(currentLastModifiedTime)) {
                        currentLastModifiedTime = lastModifiedTime; // Atualiza o tempo
                        reloadProperties(); // Recarrega as propriedades
                    }
                    Thread.sleep(Duration.ofSeconds(10L));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
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
