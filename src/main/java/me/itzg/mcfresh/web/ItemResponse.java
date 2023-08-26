package me.itzg.mcfresh.web;

/**
 * Used for wrapping non-object (single and list) values
 */
public record Response<T>(
    T data
) {

}
