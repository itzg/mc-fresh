package me.itzg.mcfresh.loaders;

public record ModDetails(
    String id,
    String name,
    String version,
    String loader,
    String loaderVersion,
    String minecraftVersion
) {

}
