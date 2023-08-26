package me.itzg.mcfresh.repos;

/**
 * @param status
 * @param version a semi-human identifier of the version
 * @param details Provides info that the repo manager needs to identify this upgrade
 */
public record UpgradeInfo(
    Status status,
    String version,
    RepoModDetails details
) {

    public enum Status {
        hasUpgrade,
        upToDate,
        unknown
    }
}
