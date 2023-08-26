package me.itzg.mcfresh.repos;

import java.nio.file.Path;
import reactor.core.publisher.Mono;

public interface RepoProvider {

    Mono<UpgradeInfo> checkForUpgrade(String loader, String minecraft, Path modFile);
}
