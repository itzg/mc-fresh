package me.itzg.mcfresh.repos.modrinth;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "repos.modrinth")
public record ModrinthProperties(
    @DefaultValue("https://api.modrinth.com")
    URI apiBaseUrl
) {

}
