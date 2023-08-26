package me.itzg.mcfresh.profiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import me.itzg.mcfresh.mods.LoaderVersion;
import me.itzg.mcfresh.mods.ModLoaderProcessor;
import me.itzg.mcfresh.profiles.ext.LauncherProfile;
import me.itzg.mcfresh.profiles.ext.LauncherProfileType;
import me.itzg.mcfresh.profiles.ext.LauncherProfiles;
import me.itzg.mcfresh.profiles.model.ModLoaderSummary;
import me.itzg.mcfresh.profiles.model.ProfileInternals;
import me.itzg.mcfresh.profiles.model.ProfileSummary;
import org.springframework.stereotype.Service;

@Service
public class ProfilesService {

    private final ObjectMapper objectMapper;
    private final ProfilesLocator profilesLocator;
    private final VersionsService versionsService;
    private final List<ModLoaderProcessor> modLoaderProcessors;

    public ProfilesService(ObjectMapper objectMapper, ProfilesLocator profilesLocator,
        VersionsService versionsService,
        List<ModLoaderProcessor> modLoaderProcessors
    ) {
        this.objectMapper = objectMapper;
        this.profilesLocator = profilesLocator;
        this.versionsService = versionsService;
        this.modLoaderProcessors = modLoaderProcessors;
    }

    public LauncherProfiles load() {
        try {
            return objectMapper.readValue(
                profilesLocator.profilesDir().resolve("launcher_profiles.json").toFile(),
                LauncherProfiles.class
            );
        } catch (IOException e) {
            throw new ProfileAccessException("Accessing launcher profiles", e);
        }
    }

    public Path getProfileGameDir(String profileId) {
        final LauncherProfiles profiles = load();
        final LauncherProfile profile = profiles.profiles().get(profileId);
        if (profile == null) {
            throw new MissingContentException("Profile does not exist");
        }

        return profile.gameDir() != null ? profile.gameDir()
             : profilesLocator.profilesDir();
    }

    public ProfileInternals getProfileInternals(String profileId) {
        final LauncherProfiles profiles = load();
        final LauncherProfile profile = profiles.profiles().get(profileId);
        if (profile == null) {
            throw new MissingContentException("Profile does not exist");
        }

        final boolean modded = hasMods(profile);

        return new ProfileInternals(profile.gameDir(), modded ? resolveLoader(profile.lastVersionId()) : null);
    }

    public List<ProfileSummary> summarize() {
        final LauncherProfiles profiles = load();

        return profiles.profiles().entrySet().stream()
            .map(entry -> {
                final String id = entry.getKey();
                final LauncherProfile profile = entry.getValue();
                final boolean modded = hasMods(profile);
                return new ProfileSummary(
                    id,
                    profile.type() == LauncherProfileType.LATEST_RELEASE ?
                        "Latest Release"
                        : profile.type() == LauncherProfileType.LATEST_SNAPSHOT ?
                            "Latest Snapshot"
                            : profile.name(),
                    profile.lastUsed(),
                    profile.type(),
                    modded,
                    modded ? resolveLoader(profile.lastVersionId()) : null
                );
            })
            .toList();
    }

    private ModLoaderSummary resolveLoader(String versionId) {
        for (ModLoaderProcessor modLoaderProcessor : modLoaderProcessors) {
            final LoaderVersion version = modLoaderProcessor.parseModLoaderVersion(versionId);
            if (version != null) {
                return new ModLoaderSummary(modLoaderProcessor.loaderId(), version);
            }
        }
        return null;
    }

    private boolean hasMods(LauncherProfile profile) {
        return profile.type() == LauncherProfileType.CUSTOM &&
            versionsService.isModded(profile.lastVersionId());
    }
}
