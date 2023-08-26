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
import me.itzg.mcfresh.mods.ModDetails;
import me.itzg.mcfresh.mods.ModLoaderProcessor;
import me.itzg.mcfresh.profiles.model.ProfileInternals;
import me.itzg.mcfresh.profiles.model.ProfileMod;
import me.itzg.mcfresh.repos.RepoModDetails;
import me.itzg.mcfresh.repos.UpgradeInfo;
import me.itzg.mcfresh.repos.UpgradeInfo.Status;
import me.itzg.mcfresh.repos.modrinth.ModrinthModDetails;
import me.itzg.mcfresh.repos.modrinth.ModrinthRepoProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProfileModsService {

    public static final String MODS_DISABLED = "mods-disabled";
    public static final String MODS = "mods";
    private final ProfilesService profilesService;
    private final List<ModLoaderProcessor> modLoaderProcessors;
    private final ModrinthRepoProvider modrinthRepoProvider;

    public ProfileModsService(ProfilesService profilesService,
        List<ModLoaderProcessor> modLoaderProcessors,
        ModrinthRepoProvider modrinthRepoProvider
    ) {
        this.profilesService = profilesService;
        this.modLoaderProcessors = modLoaderProcessors;
        this.modrinthRepoProvider = modrinthRepoProvider;
    }

    public Collection<ProfileMod> getMods(String profileId) {
        final Path gameDir = profilesService.getProfileGameDir(profileId);

        return Stream.concat(
                jarsIn(gameDir, MODS)
                    .map(path -> populateProfileMod(path, true)),
                jarsIn(gameDir, MODS_DISABLED)
                    .map(path -> populateProfileMod(path, false))
            )
            .toList();
    }

    public Mono<UpgradeInfo> checkAvailableUpgrades(String profileId, String modId) {
        final ProfileInternals internals = profilesService.getProfileInternals(profileId);

        final Path modFile = internals.gameDir().resolve(MODS).resolve(modId);
        if (Files.exists(modFile)) {
            return modrinthRepoProvider.checkForUpgrade(internals.loader().loader(), internals.loader().versions().minecraft(),
                modFile);
        }
        else {
            return Mono.just(
                new UpgradeInfo(Status.unknown, null, null)
            );
        }

    }

    public Mono<Void> upgrade(String profileId, String modId, RepoModDetails details) {
        final Path gameDir = profilesService.getProfileGameDir(profileId);

        final Path modFile = gameDir.resolve(MODS).resolve(modId);
        if (Files.exists(modFile)) {
            if (details instanceof ModrinthModDetails modrinthModDetails) {
                return modrinthRepoProvider.upgrade(modFile, modrinthModDetails);
            }
        }
        return Mono.error(new MissingContentException("Mod file does not exist"));
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

    public void enableMod(String profileId, String modId) {
        toggleMod(profileId, modId, MODS_DISABLED, MODS);
    }

    public void disableMod(String profileId, String modId) {
        toggleMod(profileId, modId, MODS, MODS_DISABLED);
    }

    private void toggleMod(String profileId, String modId, String fromSubdir, String toSubdir) {
        final Path gameDir = profilesService.getProfileGameDir(profileId);

        final Path oldFile = gameDir.resolve(fromSubdir).resolve(modId);
        if (!Files.exists(oldFile)) {
            throw new MissingContentException("Exiting mod does not exist");
        }

        final Path destDir;
        try {
            destDir = Files.createDirectories(gameDir.resolve(toSubdir));
        } catch (IOException e) {
            throw new ProfileAccessException("Setting up destination dir", e);
        }

        try {
            Files.move(oldFile, destDir.resolve(modId));
        } catch (IOException e) {
            throw new ProfileAccessException("Moving mod", e);
        }
    }
}
