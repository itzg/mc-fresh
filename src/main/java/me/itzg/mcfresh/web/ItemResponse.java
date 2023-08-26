package me.itzg.mcfresh.web;

/**
 * Used for wrapping non-object values
 */
public record ItemResponse<T>(
    T data
) {

}
