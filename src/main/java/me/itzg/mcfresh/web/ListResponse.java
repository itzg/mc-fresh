package me.itzg.mcfresh.web;

import java.util.Collection;

public record ListResponse<T>(
    Collection<T> data
) {

}
