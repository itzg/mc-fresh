package me.itzg.mcfresh.repos.modrinth.ext;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.Instant;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Version(
    String id,

    String projectId,

    String name,

    Instant datePublished,

    String versionNumber,

    VersionType versionType,

    List<VersionFile>files,

    List<String> loaders,

    List<String> gameVersions

) {

}
