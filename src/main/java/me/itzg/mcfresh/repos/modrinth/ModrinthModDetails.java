package me.itzg.mcfresh.repos.modrinth;

import lombok.EqualsAndHashCode;
import lombok.Value;
import me.itzg.mcfresh.repos.RepoModDetails;

public record ModrinthModDetails(
    String versionId
) implements RepoModDetails {

    public static final String TYPE = "modrinth";
}
