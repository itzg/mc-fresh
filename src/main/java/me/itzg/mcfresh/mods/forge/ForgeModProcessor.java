package me.itzg.mcfresh.mods.forge;

import me.itzg.mcfresh.mods.LoaderVersion;
import me.itzg.mcfresh.mods.ModDetails;
import me.itzg.mcfresh.mods.ModLoaderProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ForgeModProcessor implements ModLoaderProcessor {
    public static final Pattern VERSION_ID_PATTERN = Pattern.compile("(?<mc>.+?)-forge-(?<forge>.+?)");
    @Override
    public String loaderId() {
        return "forge";
    }

    @Override
    public LoaderVersion parseModLoaderVersion(String versionId) {
        final Matcher m = VERSION_ID_PATTERN.matcher(versionId);
        if (m.matches()) {
            return new LoaderVersion(m.group("mc"), m.group("forge"));
        }
        return null;
    }

    @Override
    public ModDetails loadModDetails(Path modFile) throws IOException {
        // process mods.toml in META-INF
        return null;
    }
}
