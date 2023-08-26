package me.itzg.mcfresh.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
    @DefaultValue("MC Fresh")
    String title,
    @DefaultValue("mc-fresh")
    String appDataSubdir,
    Path profilesDir
) {

}
