package dev.dexuby.eldenringsavemanager.file;

import org.jetbrains.annotations.NotNull;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    public static final Path WORKING_DIRECTORY_PATH = Paths.get(System.getProperty("user.dir"), "ERSaveManager");

    public byte[] loadBytes(@NotNull final File file) {

        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (final IOException ex) {
            Logger.error(ex, "Failed to read bytes from file {}.", file.getName());
        }

        return bytes;

    }

    public void saveBytes(@NotNull final File file, final byte[] bytes) {

        try {
            Files.write(file.toPath(), bytes);
        } catch (final IOException ex) {
            Logger.error(ex, "Failed write bytes to file {}.", file.getName());
        }

    }

    public List<File> getExtractedSaveFiles() {

        final File workingDirectory = WORKING_DIRECTORY_PATH.toFile();
        if (workingDirectory.exists())
            return Arrays.asList(Optional.ofNullable(workingDirectory.listFiles()).orElse(new File[0]));

        return Collections.emptyList();

    }

    public void createExtractedSaveFile(@NotNull final String name, final byte[] bytes) {

        try {
            Files.write(Path.of(WORKING_DIRECTORY_PATH.toString(), name + ".er"), bytes);
        } catch (final IOException ex) {
            Logger.error(ex, "Failed to create save file.");
        }

    }

    public void createDefaults() {

        final File workingDirectory = WORKING_DIRECTORY_PATH.toFile();
        if (!workingDirectory.exists()) {
            if (workingDirectory.mkdir()) {
                Logger.info("Created working directory at {}", WORKING_DIRECTORY_PATH);
            } else {
                Logger.warn("Failed to create working directory at {}", WORKING_DIRECTORY_PATH);
            }
        }

    }

}
