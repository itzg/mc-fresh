package me.itzg.mcfresh.profiles.model;

import java.nio.file.Path;
import java.util.Date;

public record LauncherProfile(
    Date created,
    Path gameDir,
    Date lastUsed,
    String lastVersionId,
    String name,
    LauncherProfileType type
) {

}
