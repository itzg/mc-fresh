package me.itzg.mcfresh.profiles.model;

import java.nio.file.Path;

public record ProfileInternals(
    Path gameDir,
    ModLoaderSummary loader
) {
}
