package dev.dexuby.eldenringsavemanager.save.file;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public abstract class ProcessedSaveFile {

    private final Path filePath;

    public ProcessedSaveFile(@NotNull final Path filePath) {

        this.filePath = filePath;

    }

    public Path getFilePath() {

        return this.filePath;

    }

    @Override
    public String toString() {

        return filePath.toString();

    }

}
