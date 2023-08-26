package me.itzg.mcfresh.profiles.ext;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LauncherProfileType {
    @JsonProperty("latest-release")
    LATEST_RELEASE,
    @JsonProperty("latest-snapshot")
    LATEST_SNAPSHOT,
    @JsonProperty("custom")
    CUSTOM
}
