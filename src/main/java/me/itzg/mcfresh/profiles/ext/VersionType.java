package me.itzg.mcfresh.profiles.ext;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum VersionType {
    @JsonProperty("release")
    RELEASE,
    @JsonProperty("snapshot")
    SNAPSHOT
}
