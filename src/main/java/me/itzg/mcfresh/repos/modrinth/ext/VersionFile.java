package me.itzg.mcfresh.repos.modrinth.ext;

import java.util.Map;

public record VersionFile(
    Map<String,String> hashes,

    String url,

    String filename,

    boolean primary

) {

}
