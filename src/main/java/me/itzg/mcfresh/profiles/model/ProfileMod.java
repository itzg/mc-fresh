package me.itzg.mcfresh.profiles.model;

import me.itzg.mcfresh.mods.ModDetails;

public record ProfileMod(
    String id,
    String name,
    boolean enabled,
    ModDetails details
) {

}
