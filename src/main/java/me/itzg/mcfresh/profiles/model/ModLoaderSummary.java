package me.itzg.mcfresh.profiles.model;

import me.itzg.mcfresh.mods.LoaderVersion;

public record ModLoaderSummary(
    String loader,
    LoaderVersion versions
) {
}
