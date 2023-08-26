package me.itzg.mcfresh.mods.fabric;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.itzg.mcfresh.mods.LoaderVersion;
import me.itzg.mcfresh.mods.ModDetails;
import me.itzg.mcfresh.mods.ModLoaderProcessor;
import org.springframework.stereotype.Component;

@Component
public class FabricModProcessor implements ModLoaderProcessor {

    public static final String LOADER_ID = "fabric";
    public static final Pattern VERSION_ID_PATTERN = Pattern.compile("fabric-loader-(?<fabric>.+?)-(?<mc>.+?)");

    private final ObjectMapper objectMapper;

    public FabricModProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String loaderId() {
        return LOADER_ID;
    }

    @Override
    public LoaderVersion parseModLoaderVersion(String versionId) {
        final Matcher m = VERSION_ID_PATTERN.matcher(versionId);
        if (m.matches()) {
            return new LoaderVersion(m.group("mc"), m.group("fabric"));
        }
        return null;
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
