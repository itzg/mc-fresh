package me.itzg.mcfresh.loaders;

import java.io.IOException;
import java.nio.file.Path;

public interface ModLoaderProcessor {
    String loaderId();

    /**
     * @return loaded details or null if not supported by this loader
     */
    ModDetails loadModDetails(Path modFile) throws IOException;
}
