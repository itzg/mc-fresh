package me.itzg.mcfresh.profiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import me.itzg.mcfresh.profiles.ext.LauncherVersion;
import org.springframework.stereotype.Service;

@Service
public class VersionsService {

    private final ObjectMapper objectMapper;
    private final ProfilesLocator profilesLocator;

    public VersionsService(ObjectMapper objectMapper, ProfilesLocator profilesLocator) {
        this.objectMapper = objectMapper;
        this.profilesLocator = profilesLocator;
    }

    public boolean isModded(String id) {
        final Path versionDir = profilesLocator.profilesDir().resolve("versions").resolve(id);
        final Path versionFile = versionDir.resolve("%s.json".formatted(id));

        final LauncherVersion launcherVersion;
        try {
            launcherVersion = objectMapper.readValue(versionFile.toFile(), LauncherVersion.class);
        } catch (IOException e) {
            throw new ProfileAccessException("Accessing version file", e);
        }

        return !launcherVersion.mainClass().equals("net.minecraft.client.main.Main");
    }
}
