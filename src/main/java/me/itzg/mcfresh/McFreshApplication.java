package me.itzg.mcfresh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class McFreshApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(McFreshApplication.class)
            .headless(false)
            .run(args);
    }

}
