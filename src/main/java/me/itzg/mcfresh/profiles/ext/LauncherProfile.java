package me.itzg.mcfresh.profiles.ext;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;

public record LauncherProfile(
    Instant created,
    Path gameDir,
    Instant lastUsed,
    String lastVersionId,
    String name,
    LauncherProfileType type
) {

}
