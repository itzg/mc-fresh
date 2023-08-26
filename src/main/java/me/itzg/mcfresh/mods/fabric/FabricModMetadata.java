package me.itzg.mcfresh.loaders.fabric;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FabricModMetadata(
    int schemaVersion,
    String id,
    String version,
    String name,
    Depends depends
) {

    public record Depends(
        @JsonProperty("fabricloader")
        String loaderVersion,
        String minecraft,
        String java
    ) {

    }
}
