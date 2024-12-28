package br.com.kaiquera.DemoPropertiesWatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    @Autowired
    private Environment env;

    @GetMapping("/config")
    public String getConfig() {
        return env.getProperty("config.teste");
    }
}
