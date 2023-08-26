package me.itzg.mcfresh.repos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import me.itzg.mcfresh.repos.modrinth.ModrinthModDetails;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
    @Type(name = ModrinthModDetails.TYPE, value = ModrinthModDetails.class)
})
public interface RepoModDetails {

}
