package me.itzg.mcfresh.profiles;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import me.itzg.mcfresh.loaders.ModDetails;
import me.itzg.mcfresh.loaders.ModLoaderProcessor;
import me.itzg.mcfresh.profiles.model.ProfileMod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ModsService {

    private final ProfilesService profilesService;
    private final List<ModLoaderProcessor> modLoaderProcessors;

    public ModsService(ProfilesService profilesService, List<ModLoaderProcessor> modLoaderProcessors) {
        this.profilesService = profilesService;
        this.modLoaderProcessors = modLoaderProcessors;
    }

    public Collection<ProfileMod> getMods(String profileId) {
        final Path gameDir = profilesService.getProfileGameDir(profileId);

        return Stream.concat(
                jarsIn(gameDir, "mods")
                    .map(path -> populateProfileMod(path, true)),
                jarsIn(gameDir, "mods-disabled")
                    .map(path -> populateProfileMod(path, false))
            )

            .toList();
    }

    private ProfileMod populateProfileMod(Path path, boolean enabled) {
        final String filename = path.getFileName().toString();

        final ModDetails details = loadDetails(path);

        return new ProfileMod(filename, details != null ? details.name() : filename, enabled, details);
    }

    private ModDetails loadDetails(Path jarFile) {
        for (final ModLoaderProcessor processor : modLoaderProcessors) {
            try {
                return processor.loadModDetails(jarFile);
            } catch (IOException e) {
                log.warn("Failed to load mod details", e);
            }
        }
        return null;
    }

    private Stream<Path> jarsIn(Path gameDir, String subdir) {
        final Path resolved = gameDir.resolve(subdir);
        if (Files.isDirectory(resolved)) {
            try (DirectoryStream<Path> dir = Files.newDirectoryStream(resolved, "*.jar")) {
                final List<Path> jars = new ArrayList<>();
                for (final Path jar : dir) {
                    jars.add(jar);
                }
                return jars.stream();
            } catch (IOException e) {
                throw new ProfileAccessException("Reading jars in mods dir", e);
            }
        }
        else {
            return Stream.empty();
        }
    }
}
