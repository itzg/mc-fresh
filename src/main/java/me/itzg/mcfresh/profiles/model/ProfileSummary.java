package me.itzg.mcfresh.profiles.model;

import java.time.Instant;
import me.itzg.mcfresh.profiles.ext.LauncherProfileType;

public record ProfileSummary(
    String id,
    String name,
    Instant lastUsed,
    LauncherProfileType type,
    boolean modded,
    ModLoaderSummary loader
) {

}
