package me.itzg.mcfresh.repos.modrinth;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import me.itzg.mcfresh.mods.Checksums;
import me.itzg.mcfresh.repos.RepoProvider;
import me.itzg.mcfresh.repos.UpgradeInfo;
import me.itzg.mcfresh.repos.UpgradeInfo.Status;
import me.itzg.mcfresh.repos.modrinth.ext.Version;
import me.itzg.mcfresh.repos.modrinth.ext.VersionFile;
import me.itzg.mcfresh.repos.modrinth.ext.VersionType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

@Service(ModrinthRepoProvider.REPO_ID)
@Slf4j
public class ModrinthRepoProvider implements RepoProvider {

    public static final String REPO_ID = "modrinthRepo";
    public static final ParameterizedTypeReference<List<Version>> VERSION_LIST = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;

    public ModrinthRepoProvider(WebClient.Builder webClientBuilder, ModrinthProperties modrinthProperties) {
        webClient = webClientBuilder.baseUrl(modrinthProperties.apiBaseUrl().toString())
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
            ))
            .build();
    }

    @Override
    public Mono<UpgradeInfo> checkForUpgrade(String loader,
        String minecraftVersion,
        Path modFile
    ) {
        return Mono.just(modFile)
            .publishOn(Schedulers.boundedElastic())
            .flatMap(path -> {
                try {
                    return Mono.just(Checksums.sha1(modFile));
                } catch (IOException e) {
                    return Mono.error(e);
                }
            })
            .doOnNext(hash -> log.debug("SHA-1 hash of {} is {}", modFile, hash))
            .flatMap(hash ->
                webClient.get()
                    .uri("/v2/version_file/{hash}", hash)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Version.class)
                    .doOnNext(version -> log.debug("Loaded version from current {} is {}", modFile, version))
                    .flatMap(currentVersion ->
                        checkForUpgrade(
                            currentVersion.projectId(),
                            currentVersion.versionNumber(),
                            loader,
                            minecraftVersion
                        )
                    )
                    .onErrorResume(WebClientResponseException.class, e ->
                        e.getStatusCode().equals(HttpStatus.NOT_FOUND) ?
                            Mono.just(new UpgradeInfo(Status.unknown, null, null))
                            : Mono.error(e)
                    )
            );
    }

    public Mono<UpgradeInfo> checkForUpgrade(String modId, String currentVersion, String loader,
        String minecraftVersion
    ) {
        return webClient.get()
            .uri("/v2/project/{id|slug}/version", uriBuilder -> {
                    addStringArrayQueryParam(uriBuilder, "loaders", List.of(loader));
                    addStringArrayQueryParam(uriBuilder, "game_versions", List.of(minecraftVersion));
                    return uriBuilder.build(modId);
                }
            )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(VERSION_LIST)
            .mapNotNull(versions -> versions.stream()
                .filter(version -> version.versionType() == VersionType.release
                    && version.loaders().contains(loader)
                )
                // newest is first
                .findFirst()
                .map(newest -> new UpgradeInfo(
                    currentVersion.equals(newest.versionNumber()) ? Status.upToDate : Status.hasUpgrade,
                    newest.versionNumber(),
                    new ModrinthModDetails(newest.id())
                ))
                .orElse(null));
    }

    private static void addStringArrayQueryParam(UriBuilder uriBuilder, String name, List<String> values) {
        uriBuilder
            .queryParam(name, values.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(",", "[", "]"))
            );
    }

    public Mono<Void> upgrade(Path oldModFile, ModrinthModDetails details) {
        return webClient.get()
            .uri("/v2/version/{id}", details.versionId())
            .retrieve()
            .bodyToMono(Version.class)
            .publishOn(Schedulers.boundedElastic())
            .flatMap(version -> {
                final VersionFile versionFile = version.files().stream()
                    .filter(VersionFile::primary)
                    .findFirst()
                    // fall back to first one for cases like
                    // https://modrinth.com/plugin/vane/version/v1.10.3
                    .orElse(version.files().get(0));

                final Path newModFile = oldModFile.getParent().resolve(versionFile.filename());
                log.debug("Downloading new mod file {}", newModFile);
                return
                    DataBufferUtils.write(
                            webClient.get()
                                .uri(URI.create(versionFile.url()))
                                .retrieve()
                                .bodyToFlux(DataBuffer.class),
                            newModFile
                        )
                        .thenReturn(newModFile);
            })
            .publishOn(Schedulers.boundedElastic())
            .flatMap(newModFile -> {
                log.debug("Removing old mod file {}", oldModFile);
                try {
                    //noinspection BlockingMethodInNonBlockingContext because IntelliJ is confused
                    Files.delete(oldModFile);
                } catch (IOException e) {
                    log.warn("Failed to remove old mod file {}, reverting new file", oldModFile, e);
                    try {
                        //noinspection BlockingMethodInNonBlockingContext because IntelliJ is confused
                        Files.delete(newModFile);
                    } catch (IOException ex) {
                        log.warn("Also failed to remove newly downloaded mod file {}", newModFile, e);
                    }
                }
                return Mono.empty();
            });

    }
}
