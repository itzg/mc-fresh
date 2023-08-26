package me.itzg.mcfresh.mods;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Checksums {

    public static String sha1(Path file) throws IOException {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            try (InputStream inputStream = Files.newInputStream(file)) {
                final byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) >= 0) {
                    md.update(buffer, 0, len);
                }
            }

            final byte[] digest = md.digest();

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Trying to create SHA-1 message digest", e);
        }

    }
}
