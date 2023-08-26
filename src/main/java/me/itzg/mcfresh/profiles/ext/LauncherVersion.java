package me.itzg.mcfresh.profiles.ext;

public record LauncherVersion(
    String id,
    String inheritsFrom,
    String mainClass,
    JavaVersion javaVersion
) {

    public record JavaVersion(
        String majorVersion
    ) {

    }
}
