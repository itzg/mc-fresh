package me.itzg.mcfresh.mods;

public record ModDetails(
    String id,
    String name,
    String version,
    String loader,
    String loaderVersion,
    String minecraftVersion
) {

}
