package me.itzg.mcfresh.mods.fabric;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.itzg.mcfresh.mods.ModDetails;
import me.itzg.mcfresh.mods.ModLoaderProcessor;
import org.springframework.stereotype.Component;

@Component
public class FabricProcessor implements ModLoaderProcessor {

    public static final String LOADER_ID = "fabric";

    private final ObjectMapper objectMapper;

    public FabricProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String loaderId() {
        return LOADER_ID;
    }

    @Override
    public ModDetails loadModDetails(Path modFile) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(modFile))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (entry.getName().equals("fabric.mod.json")) {
                    final FabricModMetadata metadata = objectMapper.readValue(zipIn, FabricModMetadata.class);
                    return new ModDetails(
                        metadata.id(),
                        metadata.name(),
                        metadata.version(),
                        LOADER_ID,
                        metadata.depends().loaderVersion(),
                        metadata.depends().minecraft()
                    );
                }
            }
        }
        return null;
    }
}
