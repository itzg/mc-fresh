package me.itzg.mcfresh.profiles.web;

import java.time.Instant;
import me.itzg.mcfresh.profiles.ext.LauncherProfileType;

public record ProfileSummary(
    String id,
    String name,
    Instant lastUsed,
    LauncherProfileType type
) {

}
