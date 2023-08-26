package me.itzg.mcfresh.profiles;

import java.time.Duration;
import me.itzg.mcfresh.profiles.model.ProfileMod;
import me.itzg.mcfresh.profiles.model.ProfileSummary;
import me.itzg.mcfresh.repos.RepoModDetails;
import me.itzg.mcfresh.repos.UpgradeInfo;
import me.itzg.mcfresh.web.ListResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

    private final ProfilesService profilesService;
    private final ProfileModsService profileModsService;

    public ProfilesController(ProfilesService profilesService, ProfileModsService profileModsService) {
        this.profilesService = profilesService;
        this.profileModsService = profileModsService;
    }

    @GetMapping
    public ListResponse<ProfileSummary> summarizeProfiles() {
        return new ListResponse<>(profilesService.summarize());
    }

    @GetMapping("/{profileId}/mods")
    public ListResponse<ProfileMod> modsInProfile(@PathVariable String profileId) {
        return new ListResponse<>(profileModsService.getMods(profileId));
    }

    @GetMapping("/{profileId}/mods/{modId}/availableUpgrade")
    public Mono<ResponseEntity<UpgradeInfo>> modsInProfile(@PathVariable String profileId, @PathVariable String modId) {
        return profileModsService.checkAvailableUpgrades(profileId, modId)
            .map(upgradeInfo -> ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1)))
                .body(upgradeInfo)
            );
    }

    @PostMapping("/{profileId}/mods/{modId}/upgrade")
    public Mono<Void> upgradeModInProfile(@PathVariable String profileId, @PathVariable String modId,
        @RequestBody RepoModDetails details
    ) {
        return profileModsService.upgrade(profileId, modId, details);
    }

    @PostMapping("/{profileId}/mods/{modId}/enable")
    public void enableMod(@PathVariable String profileId, @PathVariable String modId) {
        profileModsService.enableMod(profileId, modId);
    }

    @PostMapping("/{profileId}/mods/{modId}/disable")
    public void disableMod(@PathVariable String profileId, @PathVariable String modId) {
        profileModsService.disableMod(profileId, modId);
    }
}
