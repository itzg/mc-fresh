package me.itzg.mcfresh.profiles;

import java.nio.file.Files;
import java.nio.file.Path;
import me.itzg.mcfresh.config.AppProperties;
import org.springframework.stereotype.Service;

@Service
public class ProfilesLocator {

    private final AppProperties appProperties;

    public ProfilesLocator(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public Path profilesDir() {
        if (appProperties.profilesDir() != null) {
            return appProperties.profilesDir();
        }

        final String appdata = System.getenv("APPDATA");
        if (appdata != null) {
            final Path path = Path.of(appdata, ".minecraft");
            if (Files.exists(path)) {
                return path;
            }
        }

        throw new ProfileAccessException("Unable to locate minecraft profiles directory");
    }
}
