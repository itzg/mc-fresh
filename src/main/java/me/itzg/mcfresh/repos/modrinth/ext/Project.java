package me.itzg.mcfresh.repos.modrinth.ext;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Project(
    String slug,
    String id,
    String title
) {

}
