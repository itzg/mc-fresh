package me.itzg.mcfresh.mods;

import java.util.List;
import java.util.Map;
import me.itzg.mcfresh.repos.RepoProvider;
import me.itzg.mcfresh.repos.UpgradeInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ModsUpdateService {

    private final Map<String, RepoProvider> repoProviders;

    public ModsUpdateService(Map<String, RepoProvider> repoProviders) {
        this.repoProviders = repoProviders;
    }

}
